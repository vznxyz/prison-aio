package net.evilblock.prisonaio.module.combat.logger.listener

import net.evilblock.cubed.entity.living.event.EntityDamageEvent
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.combat.deathmessage.DeathMessageHandler
import net.evilblock.prisonaio.module.combat.deathmessage.listeners.DamageListener
import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.PlayerDamage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.UnknownDamage
import net.evilblock.prisonaio.module.combat.deathmessage.trackers.PVPTracker
import net.evilblock.prisonaio.module.combat.logger.CombatLogger
import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import net.evilblock.prisonaio.module.combat.logger.event.CombatLoggerDeathEvent
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.event.UserUnloadEvent
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.TimeUnit

object CombatLoggerListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val logger = CombatLoggerHandler.getLoggerByOwner(event.player.uniqueId)
        if (logger != null) {
            CombatLoggerHandler.forgetLogger(logger)
            logger.destroyForCurrentWatchers()

            if (logger.health > 0.0) {
                event.player.health = logger.health
                event.player.updateInventory()
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val timer = CombatTimerHandler.getTimer(event.player)
        if (timer != null && !timer.hasExpired()) {
            val logger = CombatLogger(event.player)
            logger.initializeData()

            CombatLoggerHandler.trackLogger(logger)
        }
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is CombatLogger) {
            val logger = event.entity as CombatLogger
            val attacker = event.attacker
            val damage = event.damage

            val trackerDamage = if (attacker is Player) {
                PVPTracker.PVPDamage(logger.owner.uniqueId, damage, attacker.uniqueId, attacker.inventory.itemInMainHand)
            } else if (attacker is Projectile && attacker.shooter is Player) {
                PVPTracker.PVPDamage(logger.owner.uniqueId, damage, attacker.uniqueId, (attacker.shooter as Player).inventory.itemInMainHand)
            } else {
                UnknownDamage(logger.owner.uniqueId, damage)
            }

            DeathMessageHandler.addDamage(logger.owner, trackerDamage)
        }
    }

    @EventHandler
    fun onCombatLoggerDeathEvent(event: CombatLoggerDeathEvent) {
        val timer = CombatTimerHandler.getTimer(event.logger.owner.uniqueId)
        if (timer != null) {
            CombatTimerHandler.forgetTimer(timer)
        }

        val deathMessage: String

        val record: List<Damage> = DeathMessageHandler.getDamageList(event.logger.owner)
        if (record.isEmpty()) {
            deathMessage = UnknownDamage(event.logger.owner.uniqueId, 1.0).getDeathMessage()
        } else {
            val deathCause = record[record.size - 1]
            if (deathCause is PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                val killerUuid = deathCause.damager

                val killer = Bukkit.getPlayer(killerUuid)
                if (killer != null) {
                    (event.logger.owner as CraftPlayer).handle.killer = (killer as CraftPlayer).handle

                    val victim = event.logger.owner

                    // prevent kill boosting
                    if (DamageListener.lastKilled.containsKey(killer.uniqueId) && DamageListener.lastKilled[killer.uniqueId]!!.first === victim.uniqueId) {
                        val kills = if (DamageListener.boosting.containsKey(killer.uniqueId)) {
                            DamageListener.boosting[killer.uniqueId]!!.first
                        } else {
                            1
                        }

                        DamageListener.boosting[killer.uniqueId] = Pair(kills, System.currentTimeMillis())
                    }

                    val sameAddress = killer.getAddress().address.hostAddress.equals(victim.address.address.hostAddress, ignoreCase = true)
                    val sameVictim = DamageListener.boosting.containsKey(killer.uniqueId) && DamageListener.boosting[killer.uniqueId]!!.first > 1

                    Tasks.async {
                        UserHandler.getOrLoadAndCacheUser(victim.uniqueId).statistics.addDeath()
                    }

                    if (!sameAddress && !sameVictim) {
                        UserHandler.getUser(killerUuid).statistics.addKill()
                        DamageListener.lastKilled[killer.uniqueId] = Pair(victim.uniqueId, System.currentTimeMillis())
                    }
                }
            }

            deathMessage = deathCause.getDeathMessage()
        }

        for (player in Bukkit.getOnlinePlayers()) {
            try {
                if (UserHandler.getUser(player.uniqueId).settings.getSettingOption(UserSetting.DEATH_MESSAGES).getValue()) {
                    player.sendMessage(deathMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        DeathMessageHandler.clearDamage(event.logger.owner)
    }

    @EventHandler
    fun onUserUnloadEvent(event: UserUnloadEvent) {
        if (CombatLoggerHandler.getLoggerByOwner(event.user.uuid) != null) {
            event.isCancelled = true
        }
    }

}
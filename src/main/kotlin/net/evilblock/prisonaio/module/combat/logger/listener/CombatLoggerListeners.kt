package net.evilblock.prisonaio.module.combat.logger.listener

import net.evilblock.cubed.entity.living.event.EntityDamageEvent
import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import net.evilblock.prisonaio.module.combat.damage.objects.Damage
import net.evilblock.prisonaio.module.combat.damage.objects.UnknownDamage
import net.evilblock.prisonaio.module.combat.damage.trackers.PVPTracker
import net.evilblock.prisonaio.module.combat.logger.CombatLogger
import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import net.evilblock.prisonaio.module.combat.logger.event.CombatLoggerDeathEvent
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.event.UserUnloadEvent
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

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

            DamageTracker.addDamage(logger.owner, trackerDamage)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCombatLoggerDeathEvent(event: CombatLoggerDeathEvent) {
        val timer = CombatTimerHandler.getTimer(event.logger.owner.uniqueId)
        if (timer != null) {
            CombatTimerHandler.forgetTimer(timer)
        }

        val record: List<Damage> = DamageTracker.getDamageList(event.logger.owner)

        val deathMessage = if (record.isEmpty()) {
            UnknownDamage(event.logger.owner.uniqueId, 1.0).getDeathMessage()
        } else {
            record[record.size - 1].getDeathMessage()
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

        DamageTracker.clearDamage(event.logger.owner)
    }

    @EventHandler
    fun onUserUnloadEvent(event: UserUnloadEvent) {
        if (CombatLoggerHandler.getLoggerByOwner(event.user.uuid) != null) {
            event.isCancelled = true
        }
    }

}
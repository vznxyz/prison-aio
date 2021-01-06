package net.evilblock.prisonaio.module.combat.deathmessage.listeners

import com.google.common.collect.Maps
import net.evilblock.prisonaio.module.combat.deathmessage.DeathMessageHandler
import net.evilblock.prisonaio.module.combat.deathmessage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.PlayerDamage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.UnknownDamage
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.TimeUnit

object DamageListener : Listener {

    internal val lastKilled: MutableMap<UUID, Pair<UUID, Long>> = Maps.newHashMap()
    internal val boosting: MutableMap<UUID, Pair<Int, Long>> = Maps.newHashMap()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val customEvent = CustomPlayerDamageEvent(event, UnknownDamage(event.entity.uniqueId, event.damage))
            customEvent.call()

            DeathMessageHandler.addDamage(event.entity as Player, customEvent.trackerDamage)
        }
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        DeathMessageHandler.clearDamage(event.player)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        DeathMessageHandler.clearDamage(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null

        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.entity.uniqueId)) {
            return
        }

        val deathMessage: String

        val record: List<Damage> = DeathMessageHandler.getDamageList(event.entity)
        if (record.isEmpty()) {
            deathMessage = UnknownDamage(event.entity.uniqueId, 1.0).getDeathMessage()
        } else {
            val deathCause = record[record.size - 1]
            if (deathCause is PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                val killerUuid = deathCause.damager

                val killer = Bukkit.getPlayer(killerUuid)
                if (killer != null) {
                    (event.entity as CraftPlayer).handle.killer = (killer as CraftPlayer).handle
                    val victim = event.entity

                    // prevent kill boosting
                    if (lastKilled.containsKey(killer.uniqueId) && lastKilled[killer.uniqueId]!!.first === victim.uniqueId) {
                        val kills = if (boosting.containsKey(killer.uniqueId)) {
                            boosting[killer.uniqueId]!!.first
                        } else {
                            1
                        }

                        boosting[killer.uniqueId] = Pair(kills, System.currentTimeMillis())
                    }

                    val sameAddress = killer.getAddress().address.hostAddress.equals(victim.address.address.hostAddress, ignoreCase = true)
                    val sameVictim = boosting.containsKey(killer.uniqueId) && boosting[killer.uniqueId]!!.first > 1

                    UserHandler.getUser(victim.uniqueId).statistics.addDeath()

                    if (!sameAddress && !sameVictim) {
                        UserHandler.getUser(killerUuid).statistics.addKill()
                        lastKilled[killer.uniqueId] = Pair(victim.uniqueId, System.currentTimeMillis())
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

        DeathMessageHandler.clearDamage(event.entity)
    }

}
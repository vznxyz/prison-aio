package net.evilblock.prisonaio.module.combat.damage.listeners

import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import net.evilblock.prisonaio.module.combat.damage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.damage.objects.Damage
import net.evilblock.prisonaio.module.combat.damage.objects.PlayerDamage
import net.evilblock.prisonaio.module.combat.damage.objects.UnknownDamage
import net.evilblock.prisonaio.module.combat.event.PlayerKilledEvent
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
import java.util.concurrent.TimeUnit

object DamageListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val customEvent = CustomPlayerDamageEvent(event, UnknownDamage(event.entity.uniqueId, event.damage))
            customEvent.call()

            DamageTracker.addDamage(event.entity as Player, customEvent.trackerDamage)
        }
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        DamageTracker.clearDamage(event.player)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        DamageTracker.clearDamage(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null

        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.entity.uniqueId)) {
            return
        }

        val deathMessage: String

        val record: List<Damage> = DamageTracker.getDamageList(event.entity)
        if (record.isEmpty()) {
            deathMessage = UnknownDamage(event.entity.uniqueId, 1.0).getDeathMessage()
        } else {
            val deathCause = record[record.size - 1]
            if (deathCause is PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                val killer = Bukkit.getPlayer(deathCause.damager)
                if (killer != null) {
                    (event.entity as CraftPlayer).handle.killer = (killer as CraftPlayer).handle

                    PlayerKilledEvent(event.entity, killer).call()
                }
            }

            deathMessage = deathCause.getDeathMessage()
        }

        for (player in Bukkit.getOnlinePlayers()) {
            try {
                if (UserHandler.isUserLoaded(player)) {
                    if (UserHandler.getUser(player).settings.getSettingOption(UserSetting.DEATH_MESSAGES).getValue()) {
                        player.sendMessage(deathMessage)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        DamageTracker.clearDamage(event.entity)
    }

}
package net.evilblock.prisonaio.module.combat.deathmessage.listeners

import com.google.common.collect.Maps
import net.evilblock.prisonaio.module.combat.deathmessage.DeathMessageHandler
import net.evilblock.prisonaio.module.combat.deathmessage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.deathmessage.event.PlayerKilledEvent
import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.PlayerDamage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.UnknownDamage
import net.evilblock.prisonaio.module.user.UserHandler.getUser
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.TimeUnit

class DamageListener : Listener {

    private val lastKilled: MutableMap<UUID, UUID> = Maps.newHashMap()
    private val boosting: MutableMap<UUID, Int> = Maps.newHashMap()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val customEvent = CustomPlayerDamageEvent(event, UnknownDamage(event.entity.uniqueId, event.damage))
            customEvent.call()

            DeathMessageHandler.addDamage(event.entity as Player, customEvent.trackerDamage)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        DeathMessageHandler.clearDamage(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null

        val record: List<Damage>? = DeathMessageHandler.getDamageList(event.entity)
        val deathMessage: String
        if (record != null) {
            val deathCause = record[record.size - 1]
            if (deathCause is PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                val killerUuid = deathCause.damager

                val killer = Bukkit.getPlayer(killerUuid)
                if (killer != null) {
                    (event.entity as CraftPlayer).handle.killer = (killer as CraftPlayer).handle
                    val victim = event.entity

                    val killedEvent = PlayerKilledEvent(killer, victim)
                    killedEvent.call()

                    // prevent kill boosting
                    if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled[killer.getUniqueId()] === victim.uniqueId) {
                        boosting.putIfAbsent(killer.getUniqueId(), 0)
                        boosting[killer.getUniqueId()] = boosting[killer.getUniqueId()]!! + 1
                    } else {
                        boosting[killer.getUniqueId()] = 0
                    }

//                    val sameAddress = killer.getAddress().address.hostAddress.equals(victim.address.address.hostAddress, ignoreCase = true)
                    val sameVictim = boosting.containsKey(killer.getUniqueId()) && boosting[killer.getUniqueId()]!! > 1

                    getUser(victim.uniqueId).statistics.addDeath()

                    if (!sameVictim) {
//                    if (!sameAddress && !sameVictim) {
                        getUser(killerUuid).statistics.addKill()
                        lastKilled[killer.getUniqueId()] = victim.uniqueId
                    }
                }
            }

            deathMessage = deathCause.getDeathMessage()
        } else {
            deathMessage = UnknownDamage(event.entity.uniqueId, 1.0).getDeathMessage()
        }

        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(deathMessage)
        }

        DeathMessageHandler.clearDamage(event.entity)
    }

}
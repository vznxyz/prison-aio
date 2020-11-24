package net.evilblock.prisonaio.module.combat.deathmessage.trackers

import net.evilblock.prisonaio.module.combat.deathmessage.DeathMessageHandler.getDamageList
import net.evilblock.prisonaio.module.combat.deathmessage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import net.evilblock.prisonaio.module.combat.deathmessage.objects.PlayerDamage
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.concurrent.TimeUnit

class FallTracker : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        val record: List<Damage> = getDamageList(event.getPlayer())
        var knocker: Damage? = null
        var knockerTime = 0L

        if (record.isNotEmpty()) {
            for (damage in record) {
                if (damage is FallDamage || damage is FallDamageByPlayer) {
                    continue
                }
                if (damage is PlayerDamage && (knocker == null || damage.time > knockerTime)) {
                    knocker = damage
                    knockerTime = damage.time
                }
            }
        }

        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis()) {
            event.trackerDamage = FallDamageByPlayer(event.getPlayer().uniqueId, event.getDamage(), (knocker as PlayerDamage).damager)
        } else {
            event.trackerDamage = FallDamage(event.getPlayer().uniqueId, event.getDamage())
        }
    }

    class FallDamage(damaged: UUID, damage: Double) : Damage(damaged, damage) {
        override fun getDeathMessage(): String {
            return "${wrapName(damaged)} hit the ground too hard."
        }
    }

    class FallDamageByPlayer(damaged: UUID, damage: Double, damager: UUID) : PlayerDamage(damaged, damage, damager) {
        override fun getDeathMessage(): String {
            return "${wrapName(damaged)} hit the ground too hard thanks to ${wrapName(damager)}."
        }
    }

}
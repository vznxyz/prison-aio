package net.evilblock.prisonaio.module.combat.damage.trackers

import net.evilblock.prisonaio.module.combat.damage.DamageTracker.getDamageList
import net.evilblock.prisonaio.module.combat.damage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.damage.objects.Damage
import net.evilblock.prisonaio.module.combat.damage.objects.PlayerDamage
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.concurrent.TimeUnit

class VoidTracker : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause.cause != EntityDamageEvent.DamageCause.VOID) {
            return
        }

        val record: List<Damage> = getDamageList(event.getPlayer())
        var knocker: Damage? = null
        var knockerTime = 0L

        if (record.isNotEmpty()) {
            for (damage in record) {
                if (damage is VoidDamage || damage is VoidDamageByPlayer) {
                    continue
                }

                if (damage is PlayerDamage && (knocker == null || damage.time > knockerTime)) {
                    knocker = damage
                    knockerTime = damage.time
                }
            }
        }

        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis()) {
            event.trackerDamage = VoidDamageByPlayer(event.getPlayer().uniqueId, event.getDamage(), (knocker as PlayerDamage).damager)
        } else {
            event.trackerDamage = VoidDamage(event.getPlayer().uniqueId, event.getDamage())
        }
    }

    class VoidDamage(damaged: UUID, damage: Double) : Damage(damaged, damage) {
        override fun getDeathMessage(): String {
            return wrapName(damaged) + " fell into the void."
        }
    }

    class VoidDamageByPlayer(damaged: UUID, damage: Double, damager: UUID) : PlayerDamage(damaged, damage, damager) {
        override fun getDeathMessage(): String {
            return wrapName(damaged) + " fell into the void thanks to " + wrapName(damager) + "."
        }
    }

}
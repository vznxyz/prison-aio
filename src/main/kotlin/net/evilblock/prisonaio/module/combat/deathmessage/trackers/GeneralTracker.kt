package net.evilblock.prisonaio.module.combat.deathmessage.trackers

import net.evilblock.prisonaio.module.combat.deathmessage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*

class GeneralTracker : Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        when (event.cause.cause) {
            EntityDamageEvent.DamageCause.SUFFOCATION -> event.trackerDamage = GeneralDamage(event.getPlayer().uniqueId, event.getDamage(), "suffocated")
            EntityDamageEvent.DamageCause.DROWNING -> event.trackerDamage = GeneralDamage(event.getPlayer().uniqueId, event.getDamage(), "drowned")
            EntityDamageEvent.DamageCause.STARVATION -> event.trackerDamage = GeneralDamage(event.getPlayer().uniqueId, event.getDamage(), "starved to death")
            EntityDamageEvent.DamageCause.LIGHTNING -> event.trackerDamage = GeneralDamage(event.getPlayer().uniqueId, event.getDamage(), "was struck by lightning")
            EntityDamageEvent.DamageCause.POISON -> event.trackerDamage = GeneralDamage(event.getPlayer().uniqueId, event.getDamage(), "was poisoned")
            EntityDamageEvent.DamageCause.WITHER -> event.trackerDamage = GeneralDamage(event.getPlayer().uniqueId, event.getDamage(), "withered away")
            else -> {}
        }
    }

    class GeneralDamage(damaged: UUID, damage: Double, private val message: String) : Damage(damaged, damage) {
        override fun getDeathMessage(): String {
            return "${wrapName(damaged)} $message."
        }
    }

}
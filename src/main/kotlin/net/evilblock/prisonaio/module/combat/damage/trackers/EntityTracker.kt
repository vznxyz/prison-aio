package net.evilblock.prisonaio.module.combat.damage.trackers

import net.evilblock.cubed.util.bukkit.EntityUtils.getName
import net.evilblock.prisonaio.module.combat.damage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.damage.objects.MobDamage
import org.bukkit.ChatColor
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

class EntityTracker : Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause is EntityDamageByEntityEvent) {
            if (event.cause.damager !is Player && event.cause.damager !is Arrow) {
                event.trackerDamage = EntityDamage(event.getPlayer().uniqueId, event.getDamage(), event.cause.damager)
            }
        }
    }

    class EntityDamage(damaged: UUID?, damage: Double, entity: Entity) : MobDamage(damaged!!, damage, entity.type) {
        override fun getDeathMessage(): String {
            return "${wrapName(damaged)} was slain by a ${ChatColor.RED}${getName(mobType)}${ChatColor.YELLOW}."
        }
    }

}
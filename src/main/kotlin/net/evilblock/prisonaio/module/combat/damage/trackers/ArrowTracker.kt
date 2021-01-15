package net.evilblock.prisonaio.module.combat.damage.trackers

import net.evilblock.cubed.util.bukkit.EntityUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.combat.damage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.damage.objects.Damage
import net.evilblock.prisonaio.module.combat.damage.objects.MobDamage
import net.evilblock.prisonaio.module.combat.damage.objects.PlayerDamage
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

class ArrowTracker : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEntityShootBow(event: EntityShootBowEvent) {
        if (event.entity is Player) {
            event.projectile.setMetadata("ShotFromDistance", FixedMetadataValue(PrisonAIO.instance, event.projectile.location))
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause is EntityDamageByEntityEvent) {
            val entityDamageByEntityEvent = event.cause
            if (entityDamageByEntityEvent.damager is Arrow) {
                val arrow = entityDamageByEntityEvent.damager as Arrow

                when (arrow.shooter) {
                    is Player -> {
                        val shooter = arrow.shooter as Player
                        for (value in arrow.getMetadata("ShotFromDistance")) {
                            val shotFrom = value.value() as Location
                            val distance = shotFrom.distance(event.getPlayer().location)
                            event.trackerDamage = ArrowDamageByPlayer(event.getPlayer().uniqueId, event.getDamage(), shooter.uniqueId, shotFrom, distance)
                        }
                    }
                    is Entity -> {
                        event.trackerDamage = ArrowDamageByMob(event.getPlayer().uniqueId, event.getDamage(), arrow.shooter as Entity)
                    }
                    else -> {
                        event.trackerDamage = ArrowDamage(event.getPlayer().uniqueId, event.getDamage())
                    }
                }
            }
        }
    }

    class ArrowDamage(damaged: UUID, damage: Double) : Damage(damaged, damage) {
        override fun getDeathMessage(): String {
            return wrapName(damaged) + " was shot."
        }
    }

    class ArrowDamageByPlayer(damaged: UUID, damage: Double, damager: UUID, private val shotFrom: Location, private val distance: Double) : PlayerDamage(damaged, damage, damager) {
        override fun getDeathMessage(): String {
            return "${wrapName(damaged)} was shot by ${wrapName(damager)} from ${ChatColor.BLUE}${distance.toInt()} blocks${ChatColor.YELLOW}."
        }
    }

    class ArrowDamageByMob(damaged: UUID, damage: Double, damager: Entity) : MobDamage(damaged, damage, damager.type) {
        override fun getDeathMessage(): String {
            return "${wrapName(damaged)} was shot by a ${ChatColor.RED}${EntityUtils.getName(mobType)}${ChatColor.YELLOW}."
        }
    }

}
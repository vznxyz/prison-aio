package net.evilblock.prisonaio.module.combat.damage.trackers

import net.evilblock.cubed.util.bukkit.EventUtils
import net.evilblock.cubed.util.bukkit.ItemUtils.getChatName
import net.evilblock.prisonaio.module.combat.damage.event.CustomPlayerDamageEvent
import net.evilblock.prisonaio.module.combat.damage.objects.PlayerDamage
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class PVPTracker : Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause is EntityDamageByEntityEvent) {
            val attacker = EventUtils.getAttacker(event.cause.damager)
            if (attacker != null) {
                val damaged = event.getPlayer()
                event.trackerDamage = PVPDamage(damaged.uniqueId, event.getDamage(), attacker.uniqueId, attacker.itemInHand)
            }
        }
    }

    class PVPDamage(damaged: UUID, damage: Double, damager: UUID, private val weapon: ItemStack) : PlayerDamage(damaged, damage, damager) {
        override fun getDeathMessage(): String {
            val itemString = if (weapon.type == Material.AIR) {
                "their fists"
            } else {
                getChatName(weapon)
            }

            val extension = " using ${ChatColor.RED}$itemString${ChatColor.YELLOW}."
            return "${wrapName(damaged)} was slain by ${wrapName(damager)}$extension"
        }
    }

}
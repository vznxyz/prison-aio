package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerItemDamageEvent

object VanillaMechanicsListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            when (event.cause) {
                EntityDamageEvent.DamageCause.FALL -> {
                    if (MechanicsModule.isFallDamageDisabled()) {
                        event.isCancelled = true
                    }
                }
                EntityDamageEvent.DamageCause.SUFFOCATION -> {
                    if (MechanicsModule.isSuffocationDamageDisabled()) {
                        event.isCancelled = true
                    }
                }
                EntityDamageEvent.DamageCause.DROWNING -> {
                    if (MechanicsModule.isDrowningDamageDisabled()) {
                        event.isCancelled = true
                    }
                }
                EntityDamageEvent.DamageCause.VOID -> {
                    event.isCancelled = true

                    event.entity.teleport(Bukkit.getServer().worlds[0].spawnLocation)
                    event.entity.sendMessage("${ChatColor.YELLOW}You have been teleported to spawn.")
                }
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.LAVA -> {
                    event.isCancelled = true
                }
                else -> {}
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (MechanicsModule.isItemDamageDisabled()) {
            event.isCancelled = true
            event.player.updateInventory()
        }
    }

}
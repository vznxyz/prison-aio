package net.evilblock.prisonaio.module.cell.listener

import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

object CellEntityListeners : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity.world == CellHandler.getGridWorld()) {
            if (event.entity is Player) {
                event.isCancelled = true
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.inventory.InventoryType

object DisableHoppersListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInventoryPickupItemEvent(event: InventoryPickupItemEvent) {
        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryMoveItemEvent(event: InventoryMoveItemEvent) {
        if (event.source.type == InventoryType.HOPPER) {
            event.isCancelled = true
        }
    }

}
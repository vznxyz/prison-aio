/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.listener

import net.evilblock.prisonaio.module.region.selection.RegionSelection
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

object RegionWandListeners : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val itemInHand = event.player.inventory.itemInHand
        if (itemInHand != null && itemInHand == RegionSelection.SELECTION_ITEM) {
            val isUpdate = event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK
            if (isUpdate) {
                val isLeftClick = event.action == Action.LEFT_CLICK_BLOCK
                val point = if (isLeftClick) 1 else 2

                val selection = RegionSelection.getSelection(event.player)

                val blockCount = if (selection != null) {
                    (selection.upperX - selection.lowerX).coerceAtLeast(1) * (selection.upperY - selection.lowerY).coerceAtLeast(1) * (selection.upperZ - selection.lowerZ).coerceAtLeast(1)
                } else {
                    0
                }

                // update selection point
                RegionSelection.setSelectionPoint(event.player, point, event.clickedBlock.location)

                // send update message with format (point, x, y, z, blockCount)
                event.player.sendMessage(RegionSelection.UPDATED_SELECTION.format(point, event.clickedBlock.location.blockX, event.clickedBlock.location.blockY, event.clickedBlock.location.blockZ, blockCount))

                // cancel the event
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack == RegionSelection.SELECTION_ITEM) {
            event.itemDrop.remove()
        }
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        event.drops.removeIf { itemStack -> itemStack == RegionSelection.SELECTION_ITEM }
    }

}
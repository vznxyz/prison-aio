package net.evilblock.prisonaio.module.crate.key.listener

import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryDragEvent

object CrateKeyDupeListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onInventoryCreativeEvent(event: InventoryCreativeEvent) {
        if (event.clickedInventory == null) {
            return
        }

        if (event.whoClicked.gameMode != GameMode.CREATIVE) {
            return
        }

        if (CrateKeyHandler.isCrateKeyItemStack(event.cursor)) {
            event.isCancelled = true
            return
        }

        val itemClicked = event.clickedInventory.getItem(event.slot)
        if (itemClicked != null) {
            if (CrateKeyHandler.isCrateKeyItemStack(itemClicked)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.whoClicked.gameMode != GameMode.CREATIVE) {
            return
        }

        if (event.clickedInventory == null) {
            if (event.cursor != null && CrateKeyHandler.isCrateKeyItemStack(event.cursor)) {
                event.isCancelled = true
            }

            return
        }

        val itemClicked = event.clickedInventory.getItem(event.slot)
        if (itemClicked != null) {
            if (CrateKeyHandler.isCrateKeyItemStack(itemClicked)) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Cancels any drag involving a crate key item.
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryDragEvent(event: InventoryDragEvent) {
        if (event.whoClicked.gameMode != GameMode.CREATIVE) {
            return
        }

        if (CrateKeyHandler.isCrateKeyItemStack(event.cursor) || CrateKeyHandler.isCrateKeyItemStack(event.oldCursor)) {
            event.isCancelled = true
            return
        }

        for (item in event.newItems.values) {
            if (CrateKeyHandler.isCrateKeyItemStack(item)) {
                event.isCancelled = true
                return
            }
        }
    }

}
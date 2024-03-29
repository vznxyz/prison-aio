/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.bank.listener

import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryDragEvent

object BankNoteDupeListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onInventoryCreativeEvent(event: InventoryCreativeEvent) {
        if (event.clickedInventory == null) {
            return
        }

        if (event.whoClicked.gameMode != GameMode.CREATIVE) {
            return
        }

        if (BankNoteHandler.isBankNoteItemStack(event.cursor)) {
            event.isCancelled = true
            return
        }

        val itemClicked = event.clickedInventory.getItem(event.slot)
        if (itemClicked != null) {
            if (BankNoteHandler.isBankNoteItemStack(itemClicked)) {
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
            if (event.cursor != null && BankNoteHandler.isBankNoteItemStack(event.cursor)) {
                event.isCancelled = true
            }

            return
        }

        val itemClicked = event.clickedInventory.getItem(event.slot)
        if (itemClicked != null) {
            if (BankNoteHandler.isBankNoteItemStack(itemClicked)) {
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

        if (BankNoteHandler.isBankNoteItemStack(event.cursor) || BankNoteHandler.isBankNoteItemStack(event.oldCursor)) {
            event.isCancelled = true
            return
        }

        for (item in event.newItems.values) {
            if (BankNoteHandler.isBankNoteItemStack(item)) {
                event.isCancelled = true
                return
            }
        }
    }

}
package net.evilblock.prisonaio.module.user.bank.listener

import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import net.evilblock.prisonaio.module.user.bank.menu.BankNoteDetailsMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

object BankNoteAdminListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.clickedInventory == null) {
            return
        }

        if (event.click != ClickType.CREATIVE) {
            return
        }

        if (event.whoClicked.gameMode != GameMode.CREATIVE || !event.whoClicked.hasPermission(Permissions.BANK_NOTES_ADMIN)) {
            return
        }

        val itemClicked = event.clickedInventory.getItem(event.slot)
        if (itemClicked != null) {
            if (BankNoteHandler.isBankNoteItemStack(itemClicked)) {
                event.isCancelled = true

                val bankNote = BankNoteHandler.findBankNote(BankNoteHandler.extractId(itemClicked)) ?: return

                BankNoteDetailsMenu(bankNote).openMenu(event.whoClicked as Player)
            }
        }
    }

}
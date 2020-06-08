package net.evilblock.prisonaio.module.crate.key.listener

import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import net.evilblock.prisonaio.module.crate.key.menu.KeyDetailsMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

object CrateKeyAdminListeners : Listener {

//    @EventHandler(ignoreCancelled = true)
//    fun onInventoryClickEvent(event: InventoryClickEvent) {
//        if (event.clickedInventory == null) {
//            return
//        }
//
//        if (event.click != ClickType.CREATIVE) {
//            return
//        }
//
//        if (event.whoClicked.gameMode != GameMode.CREATIVE || !event.whoClicked.hasPermission(Permissions.CRATES_ADMIN)) {
//            return
//        }
//
//        val itemClicked = event.clickedInventory.getItem(event.slot)
//        if (itemClicked != null) {
//            if (CrateKeyHandler.isCrateKeyItemStack(itemClicked)) {
//                event.isCancelled = true
//
//                val crateKey = CrateKeyHandler.extractKey(itemClicked) ?: return
//
//                KeyDetailsMenu(crateKey).openMenu(event.whoClicked as Player)
//            }
//        }
//    }

}
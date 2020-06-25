/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.listener

import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.placed.PlacedCrate
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import net.evilblock.prisonaio.module.crate.reward.menu.EditCrateRewardIconMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object CrateSetupListeners : Listener {

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        val openMenu = Menu.currentlyOpenedMenus[event.whoClicked.uniqueId]
        if (openMenu is EditCrateRewardIconMenu) {
            if (event.clickedInventory != null && event.currentItem != null) {
                openMenu.onSelectItemStack(event.whoClicked as Player, event.currentItem)
                openMenu.update(event.whoClicked as Player)
            }
        }
    }

    /**
     * Handles the selection handler, which is used by the "un-link" functionality.
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.LEFT_CLICK_BLOCK && PlacedCrateHandler.hasSelectionHandlerAttached(event.player)) {
            PlacedCrateHandler.handleSelection(event.player, event.clickedBlock)
        }
    }

    /**
     * Handles placement of crates.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (!PlacedCrateHandler.isChestType(event.blockPlaced.type)) {
            return
        }

        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        if (isCrateItemStack(itemInHand)) {
            event.isCancelled = true

            // check if player has permission to place crate
            if (!player.hasPermission(Permissions.CRATES_ADMIN)) {
                player.sendMessage("${ChatColor.RED}You don't have permission to place crates.")
                player.inventory.itemInMainHand = null
                player.updateInventory()
                return
            }

            // try and extract the crate instance using the ID encoded in the hidden lore
            val crate = CrateHandler.findCrate(HiddenLore.extractHiddenString(itemInHand.itemMeta.lore.first())!!)
            if (crate == null) {
                player.sendMessage("${ChatColor.RED}That crate doesn't seem to exist anymore.")
                return
            }

            // don't cancel at this point, that way we don't affect the block placement mechanics, like block direction
            event.isCancelled = false

            // create the PlacedCrate instance and start tracking it
            val placedCrate = PlacedCrate(crate, event.blockPlaced.location)

            PlacedCrateHandler.trackPlacedCrate(placedCrate)
            PlacedCrateHandler.saveData()

            player.sendMessage("${ChatColor.GREEN}Successfully placed a ${crate.name} ${ChatColor.GREEN}crate.")
        }
    }

    private fun isCrateItemStack(itemStack: ItemStack?): Boolean {
        if (itemStack == null || itemStack.type == Material.AIR) {
            return false
        }

        if (!PlacedCrateHandler.isChestType(itemStack.type)) {
            return false
        }

        if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName() || !itemStack.itemMeta.hasLore()) {
            return false
        }

        if (!HiddenLore.hasHiddenString(itemStack.itemMeta.lore.first())) {
            return false
        }

        return true
    }

}
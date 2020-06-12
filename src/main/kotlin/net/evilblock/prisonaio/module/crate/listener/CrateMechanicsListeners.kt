package net.evilblock.prisonaio.module.crate.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import net.evilblock.prisonaio.module.crate.menu.CratePreviewMenu
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import net.evilblock.prisonaio.module.crate.roll.CrateRoll
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

object CrateMechanicsListeners : Listener {

    /**
     * Prevents blocks attached to crates from being broken.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (PlacedCrateHandler.isAttachedToCrate(event.block)) {
            event.isCancelled = true
        }
    }

    /**
     * Handles the "preview" and "open" functions.
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        // prevent placing crate keys
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (CrateKeyHandler.isCrateKeyItemStack(event.player.inventory.itemInMainHand)) {
                event.isCancelled = true
            }
        }

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.clickedBlock == null) {
                return
            }

            if (!PlacedCrateHandler.isAttachedToCrate(event.clickedBlock)) {
                return
            }

            // always prevent the block container from being opened
            event.isCancelled = true

            val placedCrate = PlacedCrateHandler.getPlacedCrate(event.clickedBlock)
            if (!placedCrate.crate.isSetup()) {
                event.player.sendMessage("${ChatColor.RED}That crate has not been setup completely!")
                return
            }

            // left-click means we're previewing the crate
            // right-click means we're attempting to roll the crate
            if (event.action == Action.LEFT_CLICK_BLOCK) {
                CratePreviewMenu(placedCrate.crate).openMenu(event.player)
            } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                val itemInHand = event.player.inventory.itemInMainHand

                if (!CrateKeyHandler.isCrateKeyItemStack(itemInHand)) {
                    return
                }

                val crate = CrateKeyHandler.extractCrate(itemInHand)
                if (crate != placedCrate.crate) {
                    event.player.sendMessage("${ChatColor.RED}This key doesn't go to that crate!")
                    return
                }

                if (event.player.inventory.itemInMainHand.amount == 1) {
                    event.player.inventory.itemInMainHand = null
                    event.player.updateInventory()
                } else {
                    event.player.inventory.itemInMainHand.amount = event.player.inventory.itemInMainHand.amount - 1
                    event.player.updateInventory()
                }

                Tasks.async {
                    CrateRoll(placedCrate).finish(event.player)
                }
            }
        }
    }

}
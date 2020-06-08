package net.evilblock.prisonaio.module.crate.listener

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import net.evilblock.prisonaio.module.crate.menu.CratePreviewMenu
import net.evilblock.prisonaio.module.crate.placed.PlacedCrate
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import net.evilblock.prisonaio.module.crate.roll.CrateRoll
import net.evilblock.prisonaio.module.crate.roll.CrateRollHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
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

            if (event.action == Action.LEFT_CLICK_BLOCK) {
                // left-click means we're previewing the crate
                CratePreviewMenu(placedCrate.crate).openMenu(event.player)
            } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                // right-click means we're attempting to roll the crate
                val itemInHand = event.player.inventory.itemInMainHand

                if (!CrateKeyHandler.isCrateKeyItemStack(itemInHand)) {
                    return
                }

//                val crateKey = CrateKeyHandler.extractKey(itemInHand)
//                if (crateKey == null) {
//                    event.player.sendMessage("${ChatColor.RED}This key doesn't seem to be valid.")
//                    return
//                }

                val crate = CrateKeyHandler.extractCrate(itemInHand)
                if (crate != placedCrate.crate) {
                    event.player.sendMessage("${ChatColor.RED}This key doesn't go to that crate!")
                    return
                }

                PrisonAIO.instance.server.scheduler.runTaskAsynchronously(PrisonAIO.instance) {
//                    attemptRoll(event.player, placedCrate, crateKey)
                    attemptRoll(event.player, placedCrate)
                }
            }
        }
    }

//    private fun attemptRoll(player: Player, placedCrate: PlacedCrate, crateKey: CrateKey) {
    private fun attemptRoll(player: Player, placedCrate: PlacedCrate) {
//        if (crateKey.uses >= crateKey.maxUses) {
//            player.sendMessage("${ChatColor.RED}The key you're trying to use seems to be duplicated and cannot be used. If you believe this is an error, please contact the support team.")
//            crateKey.dupedUseAttempts++
//            return
//        }

        if (!placedCrate.crate.isSetup()) {
            player.sendMessage("${ChatColor.RED}That crate has not been setup completely!")
            return
        }

        if (CrateRollHandler.isRolling(player)) {
            val activeRoll = CrateRollHandler.getActiveRoll(player)
            // TODO: player rerolls > 0
            if (activeRoll.placedCrate.crate.reroll) {
                return
            }

            activeRoll.finish(player) // finish the active roll
            CrateRollHandler.forgetRoll(activeRoll) // forget the active roll
        }

//        crateKey.uses++

        val roll = CrateRoll(player, placedCrate)
        CrateRollHandler.trackRoll(roll)

        if (player.inventory.itemInMainHand.amount == 1) {
            player.inventory.itemInMainHand = null
            player.updateInventory()
        } else {
            player.inventory.itemInMainHand.amount = player.inventory.itemInMainHand.amount - 1
            player.updateInventory()
        }
    }

}
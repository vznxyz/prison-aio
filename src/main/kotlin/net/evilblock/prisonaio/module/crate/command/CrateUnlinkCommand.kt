/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CrateUnlinkCommand {

    @Command(
        names = ["crate unlink", "prison crate unlink"],
        description = "Un-link a placed crate",
        permission = "prisonaio.crates.unlink"
    )
    @JvmStatic
    fun execute(player: Player) {
        PlacedCrateHandler.attachSelectionHandler(player) { block ->
            if (PlacedCrateHandler.isAttachedToCrate(block)) {
                val crate = PlacedCrateHandler.getPlacedCrate(block)
                crate.destroy()

                PlacedCrateHandler.forgetPlacedCrate(crate)
                PlacedCrateHandler.saveData()

                player.sendMessage("${ChatColor.GREEN}Crate has been unlinked!")
            } else {
                player.sendMessage("${ChatColor.RED}Crate is not linked!")
            }
        }

        player.sendMessage("${ChatColor.GREEN}Break the crate that you would like to un-link...")
    }

}
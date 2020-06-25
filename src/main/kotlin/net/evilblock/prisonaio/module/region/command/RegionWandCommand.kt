/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.region.selection.RegionSelection
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionWandCommand {

    @Command(
        names = ["region wand"],
        description = "Give yourself the region selection wand",
        permission = "training.mines.wand"
    )
    @JvmStatic
    fun execute(player: Player) {
        player.inventory.addItem(RegionSelection.SELECTION_ITEM)
        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}You have given yourself the region selection wand.")
    }

}
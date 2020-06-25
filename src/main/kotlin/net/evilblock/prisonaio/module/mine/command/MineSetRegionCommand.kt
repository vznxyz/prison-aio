/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.region.selection.RegionSelection
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineSetRegionCommand {

    @Command(
        names = ["mine setregion"],
        description = "Set the region of a mine",
        permission = "prisonaio.mines.setregion",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        if (!RegionSelection.hasSelection(player)) {
            player.sendMessage(RegionSelection.FINISH_SELECTION)
            return
        }

        if (mine.region != null) {
            RegionsModule.clearBlockCache(mine)
        }

        // update the mine's region to the player's selection
        mine.region = RegionSelection.getSelection(player)

        // make changes to block cache
        RegionsModule.updateBlockCache(mine)

        // save changes to file
        MineHandler.saveData()

        // send update message
        player.sendMessage("${ChatColor.GREEN}Updated mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}'s region.")
    }

}
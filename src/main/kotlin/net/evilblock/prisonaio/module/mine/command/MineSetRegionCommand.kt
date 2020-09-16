/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineSetRegionCommand {

    @Command(
        names = ["mine set-region"],
        description = "Set the region of a mine",
        permission = Permissions.MINES_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        val selection = WorldEditUtils.getSelection(player)
        if (selection == null) {
            player.sendMessage("${ChatColor.RED}You need to select a region using the WorldEdit wand!")
            return
        }

        if (mine.region != null) {
            RegionHandler.clearBlockCache(mine)
        }

        // update the mine's region to the player's selection
        mine.region = WorldEditUtils.toCuboid(selection)
        mine.cacheChunks()

        // make changes to block cache
        RegionHandler.updateBlockCache(mine)

        // save changes to file
        MineHandler.saveData()

        // send update message
        player.sendMessage("${ChatColor.GREEN}Updated mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}'s region.")
    }

}
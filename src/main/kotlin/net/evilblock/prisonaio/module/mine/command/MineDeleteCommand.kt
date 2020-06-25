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
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineDeleteCommand {

    @Command(
        names = ["mine delete"],
        description = "Delete an existing mine",
        permission = "prisonaio.mines.delete",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        RegionsModule.clearBlockCache(mine)
        MineHandler.deleteMine(mine)

        player.sendMessage("${ChatColor.GREEN}Deleted mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}.")
    }

}
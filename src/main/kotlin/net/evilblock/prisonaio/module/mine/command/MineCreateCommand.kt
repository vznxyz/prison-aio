/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineCreateCommand {

    @Command(
        names = ["mine create"],
        description = "Create a new mine",
        permission = "prisonaio.mines.create",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name") name: String) {
        if (MineHandler.getMineById(name).isPresent) {
            player.sendMessage("${ChatColor.RED}Mines must have a unique name and `${ChatColor.WHITE}$name${ChatColor.RED}` is already taken.")
            return
        }

        val mine = MineHandler.createMine(name)
        MineHandler.saveData()

        RegionsModule.updateBlockCache(mine)

        player.sendMessage("${ChatColor.GREEN}Created new mine ${ChatColor.WHITE}$name${ChatColor.GREEN}.")
    }

}
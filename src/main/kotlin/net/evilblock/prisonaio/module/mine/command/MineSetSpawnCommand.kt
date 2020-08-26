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
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineSetSpawnCommand {

    @Command(
        names = ["mine set-spawn"],
        description = "Set the spawn point of a mine",
        permission = "prisonaio.mines.setspawn",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        // update the mine's region to the player's selection
        mine.spawnPoint = player.location

        // save changes to file
        MineHandler.saveData()

        // send update message
        player.sendMessage("${ChatColor.GREEN}Updated mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}'s spawn point.")
    }

}
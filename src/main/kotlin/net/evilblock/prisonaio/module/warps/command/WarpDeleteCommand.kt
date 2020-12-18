/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warps.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.warps.Warp
import net.evilblock.prisonaio.module.warps.WarpHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object WarpDeleteCommand {

    @Command(
        names = ["warp delete", "warps delete", "delwarp", "delete-warp"],
        description = "Deletes a warp",
        permission = Permissions.WARPS_MANAGE,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "warp") warp: Warp) {
        WarpHandler.forgetWarp(warp)
        WarpHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully deleted warp ${ChatColor.WHITE}${warp.id}${ChatColor.GREEN}!")
    }

}
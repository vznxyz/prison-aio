/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.warp.Warp
import net.evilblock.prisonaio.module.warp.WarpHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object WarpCreateCommand {

    @Command(
        names = ["warp create", "warps create", "setwarp", "set-warp"],
        description = "Creates or updates a warp",
        permission = Permissions.WARPS_MANAGE,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name") warpId: String) {
        var warp = WarpHandler.getWarpById(warpId)
        if (warp != null) {
            player.sendMessage("${ChatColor.RED}A warp with an ID of ${ChatColor.WHITE}$warpId ${ChatColor.RED}already exists!")
            return
        }

        warp = Warp(warpId, player.location)

        WarpHandler.trackWarp(warp)
        WarpHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully created a new warp with an ID of ${ChatColor.WHITE}`$warpId`${ChatColor.GREEN}.")
    }

}
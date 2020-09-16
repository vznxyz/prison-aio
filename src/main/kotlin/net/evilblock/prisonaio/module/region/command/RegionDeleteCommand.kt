/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionDeleteCommand {

    @Command(
        names = ["region delete"],
        description = "Delete an existing region",
        permission = Permissions.REGION_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "region") region: Region) {
        RegionHandler.forgetRegion(region)
        RegionHandler.clearBlockCache(region)
        RegionHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully deleted the `${region.id}` region.")
    }

}
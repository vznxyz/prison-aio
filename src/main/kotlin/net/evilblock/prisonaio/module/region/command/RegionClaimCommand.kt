/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionClaimCommand {

    @Command(
        names = ["region claim"],
        description = "Updates a region's claim",
        permission = Permissions.REGION_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "region") region: Region) {
        val selection = WorldEditUtils.getSelection(player)
        if (selection == null) {
            player.sendMessage("${ChatColor.RED}You need to select a region using the `/region wand`.")
            return
        }

        RegionHandler.clearBlockCache(region)

        region.setCuboid(WorldEditUtils.toCuboid(selection))
        RegionHandler.updateBlockCache(region)
        RegionHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully updated the region of the `${region.id}` region.")
    }

}
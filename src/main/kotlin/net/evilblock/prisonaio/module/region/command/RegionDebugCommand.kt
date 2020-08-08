/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object RegionDebugCommand {

    @Command(
        names = ["region debug", "rg debug"],
        description = "Prints debug information about Regions",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val region = RegionsModule.findRegion(player.location)
        player.sendMessage("Standing in ${region.getRegionName()}")
    }

}
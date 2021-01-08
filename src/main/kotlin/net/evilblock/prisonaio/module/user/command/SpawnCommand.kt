/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(
        names = ["spawn"],
        description = "Teleport to spawn"
    )
    @JvmStatic
    fun execute(player: Player) {
        val region = RegionHandler.findRegion(player)
        if (region is BitmaskRegion) {
            if (region.hasBitmask(RegionBitmask.DANGER_ZONE)) {

            }
        }

        player.teleport(PrisonAIO.instance.getSpawnLocation())
        player.sendMessage("${ChatColor.YELLOW}You've been teleported to spawn!")
    }

}
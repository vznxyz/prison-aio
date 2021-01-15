/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.teleport.UserTeleport
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
                if (RegionBypass.hasBypass(player)) {
                    RegionBypass.attemptNotify(player)
                    player.teleport(PrisonAIO.instance.getSpawnLocation())
                    player.sendMessage("${ChatColor.YELLOW}You've been teleported to spawn!")
                    return
                }

                val user = UserHandler.getUser(player)

                user.pendingTeleport = UserTeleport(
                    name = "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Spawn",
                    location = player.location.clone(),
                    duration = 10_000L
                ) { success ->
                    if (success) {
                        Tasks.sync {
                            player.teleport(PrisonAIO.instance.getSpawnLocation())
                            player.sendMessage("${ChatColor.YELLOW}You've been teleported to spawn!")
                        }
                    }
                }

                player.sendMessage("${ChatColor.YELLOW}Teleporting you to ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Spawn ${ChatColor.YELLOW}in 10 seconds... Stay still and do not take damage.")
                return
            }
        }

        player.teleport(PrisonAIO.instance.getSpawnLocation())
        player.sendMessage("${ChatColor.YELLOW}You've been teleported to spawn!")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player

object RegionBypassCommand {

    @Command(
        names = ["region bypass", "rg bypass"],
        description = "Grants bypass for interactions in regions owned by others",
        permission = Permissions.REGION_BYPASS,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val bypass = !RegionBypass.hasBypass(player)
        RegionBypass.setBypass(player, bypass)

        if (bypass) {
            if (player.gameMode != GameMode.CREATIVE) {
                player.gameMode = GameMode.CREATIVE
            }
        } else {
            if (player.gameMode == GameMode.CREATIVE) {
                player.gameMode = GameMode.SURVIVAL
            }
        }

        val color = if (bypass) { ChatColor.GREEN } else { ChatColor.RED }
        player.sendMessage("${ChatColor.YELLOW}You have toggled your region bypass to: $color$bypass")
    }

}
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
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineTeleportCommand {

    @Command(
        names = ["mine teleport", "mine tp"],
        description = "Open the mine editor",
        permission = Permissions.MINES_ADMIN
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        if (mine.spawnPoint != null) {
            player.teleport(mine.spawnPoint!!)
            player.sendMessage("${ChatColor.YELLOW}You teleported to the ${ChatColor.BLUE}${mine.id} ${ChatColor.YELLOW}mine.")
        }
    }

}
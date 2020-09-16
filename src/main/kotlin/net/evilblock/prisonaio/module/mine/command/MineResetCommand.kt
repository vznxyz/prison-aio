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

object MineResetCommand {

    @Command(
        names = ["mine reset"],
        description = "Open the mine editor",
        permission = Permissions.MINES_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        if (mine.getBreakableCuboid() != null) {
            mine.resetRegion()
            player.sendMessage("${ChatColor.GREEN}You successfully reset the ${mine.id} mine.")
        } else {
            player.sendMessage("${ChatColor.RED}That mine doesn't have a region set.")
        }
    }

}
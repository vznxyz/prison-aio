/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object MineListCommand {

    @Command(
        names = ["mine list"],
        description = "Lists all of the mine IDs",
        permission = Permissions.MINES_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage(MineHandler.getMines().joinToString { it.id })
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object WipeDatabaseCommand {

    @Command(
        names = ["prison wipedb"],
        description = "Wipe the PrisonAIO database",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender is Player) {
            sender.sendMessage("${ChatColor.RED}That command must be executed from console!")
            return
        }

        PrisonAIO.instance.database.drop()
        sender.sendMessage("${ChatColor.GREEN}Successfully wiped database!")
    }

}
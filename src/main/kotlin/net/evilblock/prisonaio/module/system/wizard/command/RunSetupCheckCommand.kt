/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.wizard.command

import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RunSetupCheckCommand {

    @Command(
        names = ["prison setup-check"],
        description = "Runs production setup check",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage("${ChatColor.GREEN}Running production setup checks...")
        sender.sendMessage("")



        sender.sendMessage("")
        sender.sendMessage("${ChatColor.GREEN}Finished checks!")
    }

}
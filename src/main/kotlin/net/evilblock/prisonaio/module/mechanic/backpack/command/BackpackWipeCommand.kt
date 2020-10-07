/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

object BackpackWipeCommand {

    @Command(
        names = ["backpack wipe"],
        description = "Wipes all tracked backpacks",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender !is ConsoleCommandSender) {
            sender.sendMessage("${ChatColor.RED}That command must be executed from console!")
            return
        }

        val count = BackpackHandler.getBackpacks().size

        BackpackHandler.wipeBackpacks()
        BackpackHandler.saveData()

        sender.sendMessage("${ChatColor.GOLD}Wiped $count backpacks!")
    }

}
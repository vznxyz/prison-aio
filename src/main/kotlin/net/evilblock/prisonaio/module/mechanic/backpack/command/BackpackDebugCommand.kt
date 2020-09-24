/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BackpackDebugCommand {

    @Command(
        names = ["backpack debug"],
        description = "Prints debug information about a backpack",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "id") backpack: Backpack) {
        sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}Debug of Backpack #${backpack.id}")
        sender.sendMessage("Enchants: ${backpack.upgrades.size}")
        sender.sendMessage("Contents Size: ${backpack.contents.size}")
        sender.sendMessage("Items Size: ${backpack.getItemsSize()}")
        sender.sendMessage("Max Items Size: ${backpack.getMaxItemsSize()}")
    }

}
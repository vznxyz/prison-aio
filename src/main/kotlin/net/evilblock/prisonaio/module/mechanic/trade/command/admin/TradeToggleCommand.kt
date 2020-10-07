/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.trade.TradeHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object TradeToggleCommand {

    @Command(
        names = ["trade toggle"],
        description = "Toggle the trade feature",
        permission = "prisonaio.trade.toggle"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        TradeHandler.disabled = !TradeHandler.disabled

        if (TradeHandler.disabled) {
            sender.sendMessage("${ChatColor.RED}Trading is now disabled!")
        } else {
            sender.sendMessage("${ChatColor.GREEN}Trading is now enabled!")
        }
    }

}
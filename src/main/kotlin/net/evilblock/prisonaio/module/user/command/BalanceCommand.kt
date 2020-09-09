/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BalanceCommand {

    @Command(
        names = ["balance", "bal", "money"],
        description = "Check a player's money balance",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: User) {
        sender.sendMessage("${ChatColor.WHITE}${target.getUsername()}${ChatColor.GOLD}'s balance is: ${Formats.formatMoney(target.getMoneyBalance())}")
    }

}
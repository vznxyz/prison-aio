/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object EconomyGiveCommand {

    @Command(
        names = ["economy give", "eco give", "economy add", "eco add"],
        description = "Reset a player's money balance",
        permission = Permissions.ECONOMY_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") user: User, @Param(name = "amount") amount: Double) {
        user.addMoneyBalance(amount)
        sender.sendMessage("${ChatColor.GOLD}Added ${Formats.formatMoney(user.getMoneyBalance())}${ChatColor.GOLD} to ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GOLD}'s balance!")
    }

}
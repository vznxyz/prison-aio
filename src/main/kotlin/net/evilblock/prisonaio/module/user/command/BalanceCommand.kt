/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.NumberFormat

object BalanceCommand {

    @Command(
        names = ["balance", "bal", "money"],
        description = "Check a player's money balance",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: User) {
        val context = if (sender is Player && target.uuid == sender.uniqueId) {
            "${ChatColor.GOLD}Your balance: "
        } else {
            "${ChatColor.WHITE}${target.getUsername()}'s ${ChatColor.GOLD}balance: "
        }

        FancyMessage(context)
            .then(Formats.formatMoney(target.getMoneyBalance()))
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Exact balance: ${NumberFormat.getInstance().format(target.getTokenBalance())}"))
            .send(sender)
    }

}
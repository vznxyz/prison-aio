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

object TokensCommand {

    @Command(
        names = ["token", "tokens", "token balance", "token bal", "tokens balance", "tokens bal"],
        description = "Check your tokens balance",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") user: User) {
        val context = if (sender is Player && user.uuid == sender.uniqueId) {
            "${ChatColor.GOLD}Your tokens balance: "
        } else {
            "${ChatColor.WHITE}${user.getUsername()}'s ${ChatColor.GOLD}tokens balance: "
        }

        FancyMessage(context)
            .then(Formats.formatTokens(user.getTokenBalance()))
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Exact balance: ${NumberFormat.getInstance().format(user.getTokenBalance())}"))
            .send(sender)
    }

}
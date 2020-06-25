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
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.text.NumberFormat

object TokensSetCommand {

    @Command(
        names = ["token set", "tokens set"],
        description = "Set a player's token balance",
        permission = "prisonaio.user.tokens.set",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "newBalance") newBalance: Long) {
        user.updateTokensBalance(newBalance)
        UserHandler.saveUser(user)

        val formattedBalance = NumberFormat.getInstance().format(newBalance)
        sender.sendMessage("${ChatColor.GREEN}You set ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}'s balance to ${ChatColor.YELLOW}$formattedBalance ${ChatColor.GREEN}tokens.")
    }

}
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

object TokensTakeCommand {

    @Command(
        names = ["token take", "tokens take"],
        description = "Take tokens from a player's balance",
        permission = "prisonaio.user.tokens.take",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "amount") amount: Long) {
        user.subtractTokensBalance(amount)
        UserHandler.saveUser(user)

        val formattedAmount = NumberFormat.getInstance().format(amount)
        sender.sendMessage("${ChatColor.GREEN}You've taken ${ChatColor.YELLOW}$formattedAmount ${ChatColor.GREEN}tokens from ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}'s balance.")
    }

}
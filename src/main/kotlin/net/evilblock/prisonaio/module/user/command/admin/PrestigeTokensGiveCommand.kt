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

object PrestigeTokensGiveCommand {

    @Command(
        names = ["prestigetoken give", "prestigetokens give", "pts give"],
        description = "Give prestige tokens to a player",
        permission = "prisonaio.user.prestige.tokens.give",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "tokens") amount: Int) {
        user.addPrestigeTokens(amount)
        UserHandler.saveUser(user)

        val formattedAmount = NumberFormat.getInstance().format(amount)
        sender.sendMessage("${ChatColor.GREEN}You gave ${ChatColor.WHITE}$formattedAmount ${ChatColor.GREEN}prestige tokens to ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}.")
    }

}
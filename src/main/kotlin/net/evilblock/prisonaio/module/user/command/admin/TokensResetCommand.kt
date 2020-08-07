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

object TokensResetCommand {

    @Command(
        names = ["token reset", "tokens reset"],
        description = "Reset a player's token balance",
        permission = "prisonaio.user.tokens.reset",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User) {
        user.updateTokenBalance(0)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}You reset ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}'s tokens balance.")
    }

}
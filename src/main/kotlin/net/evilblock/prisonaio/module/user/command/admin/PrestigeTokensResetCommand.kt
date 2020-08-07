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

object PrestigeTokensResetCommand {

    @Command(
        names = ["prestigetoken reset", "prestigetokens reset", "pts reset"],
        description = "Reset a player's prestige tokens",
        permission = "prisonaio.user.prestige.tokens.reset",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User) {
        user.updatePrestigeTokens(0)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}You reset ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}'s prestige tokens.")
    }

}
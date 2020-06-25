/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.battlepass.progress.BattlePass
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BattlePassResetCommand {

    @Command(
        names = ["battlepass reset", "bp reset", "junkiepass reset", "jp reset"],
        description = "Reset a user's BattlePass progress",
        permission = Permissions.BATTLE_PASS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User) {
        user.battlePassData = BattlePass(user)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}You have reset ${ChatColor.WHITE}${user.getUsername()} ${ChatColor.GREEN}'s BattlePass progress.")
    }

}
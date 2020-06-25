/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BattlePassSetPremiumCommand {

    @Command(
        names = ["battlepass setpremium", "bp setpremium", "junkiepass setpremium", "jp setpremium"],
        description = "Sets a user's BattlePass premium status",
        permission = Permissions.BATTLE_PASS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "true/false") premium: Boolean) {
        user.battlePassData.setPremium(premium)
        UserHandler.saveUser(user)

        if (premium) {
            val player = user.getPlayer()
            if (player != null) {
                player.sendMessage("")
                player.sendMessage(" ${ChatColor.GOLD}${ChatColor.BOLD}BattlePass Upgraded")
                player.sendMessage(" ${ChatColor.GRAY}Your BattlePass is now Premium! This means you")
                player.sendMessage(" ${ChatColor.GRAY}now have access to all of the premium challenges")
                player.sendMessage(" ${ChatColor.GRAY}and their rewards! Use `/bp` to view new challenges.")
                player.sendMessage("")
            }
        }

        if (premium) {
            sender.sendMessage("${ChatColor.GREEN}You have granted ${ChatColor.WHITE}${user.getUsername()} ${ChatColor.GREEN}the Premium BattlePass.")
        } else {
            sender.sendMessage("${ChatColor.GREEN}You have revoked ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}'s Premium BattlePass.")
        }
    }

}
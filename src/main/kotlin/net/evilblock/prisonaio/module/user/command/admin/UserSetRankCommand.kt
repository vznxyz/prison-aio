/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object UserSetRankCommand {

    @Command(
        names = ["user set-rank"],
        description = "Update a user's rank",
        permission = "prisonaio.user.rank.set",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "rank") rank: Rank) {
        user.updateRank(rank)
        UserHandler.saveUser(user)

        val player = Bukkit.getPlayer(user.uuid)
        if (player != null) {
            if (sender is Player) {
                player.sendMessage("${ChatColor.GREEN}Your rank has been manually updated to ${rank.displayName} ${ChatColor.GREEN}by ${ChatColor.YELLOW}${sender.name}${ChatColor.GREEN}.")
            } else {
                player.sendMessage("${ChatColor.GREEN}Your rank has been manually updated to ${rank.displayName}${ChatColor.GREEN}.")
            }
        }

        sender.sendMessage("${Constants.ADMIN_PREFIX}You've set ${ChatColor.YELLOW}${user.getUsername()}${ChatColor.GRAY}'s rank to ${rank.displayName}${ChatColor.GRAY}.")
    }

}
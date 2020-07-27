/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object IndexCommand {

    @Command(
        names = ["leaderboards", "leaderboard", "lb"],
        description = "View the official leaderboards",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}OFFICIAL LEADERBOARDS")

        for (leaderboard in LeaderboardsModule.getLeaderboards()) {
            val message = FancyMessage("${ChatColor.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.RED}${ChatColor.BOLD}${leaderboard.name} ")
                .then("${ChatColor.GRAY}[${ChatColor.GREEN}${ChatColor.BOLD}VIEW${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to view this leaderboard."))
                .command("lb results ${leaderboard.id}")

            if (sender.hasPermission("")) {
                message.then(" ")
                    .then("${ChatColor.GRAY}[${ChatColor.AQUA}${ChatColor.BOLD}SPAWN${ChatColor.GRAY}]")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to spawn this leaderboard NPC."))
                    .command("lb spawn ${leaderboard.id}")
            }

            message.send(sender)
        }
    }

}
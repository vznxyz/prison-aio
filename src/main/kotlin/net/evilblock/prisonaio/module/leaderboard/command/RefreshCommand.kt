/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RefreshCommand {

    @Command(
        names = ["leaderboards refresh", "lb refresh"],
        description = "Forcefully refresh the leaderboards data",
        permission = Permissions.LEADERBOARDS_REFRESH,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        LeaderboardsModule.refreshLeaderboards()
        sender.sendMessage("${ChatColor.GREEN}Forcefully refreshed the leaderboards data.")
    }

}
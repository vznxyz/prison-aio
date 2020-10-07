/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import org.bukkit.command.CommandSender

object ResultsCommand {

    @Command(
        names = ["leaderboards results", "leaderboard results", "lb results"],
        description = "Displays the results of a specific leaderboard",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "leaderboard") leaderboard: Leaderboard) {
        for ((index, line) in leaderboard.getDisplayLines(false).withIndex()) {
            sender.sendMessage(line)

            if (index >= 11) {
                break
            }
        }
    }

}
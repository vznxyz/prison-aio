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
import org.bukkit.command.CommandSender

object ResultsCommand {

    @Command(names = ["leaderboards", "leaderboard", "lb"])
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "leaderboard") leaderboard: Leaderboard) {
        for (line in leaderboard.getDisplayLines()) {
            sender.sendMessage(line)
        }
    }

}
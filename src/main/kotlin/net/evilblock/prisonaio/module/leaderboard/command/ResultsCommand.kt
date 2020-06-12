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
package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RefreshCommand {

    @Command(
        names = ["leaderboards refresh", "lb refresh"],
        description = "Forcefully refresh the leaderboards data",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        LeaderboardsModule.refreshLeaderboards()
        sender.sendMessage("${ChatColor.GREEN}Forcefully refreshed the leaderboards data.")
    }

}
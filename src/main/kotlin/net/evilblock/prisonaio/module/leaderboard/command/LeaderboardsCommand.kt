/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.leaderboard.menu.LeaderboardsMenu
import org.bukkit.entity.Player

object LeaderboardsCommand {

    @Command(
        names = ["leaderboards", "leaderboard", "lb"],
        description = "View the official leaderboards",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        LeaderboardsMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ExemptionAddCommand {

    @Command(
        names = ["leaderboard exemption add", "lb exemption add", "leaderboard exempt add", "lb exempt add"],
        description = "Add a leaderboard exemption",
        permission = Permissions.LEADERBOARDS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User) {
        if (LeaderboardsModule.exemptions.contains(user.uuid)) {
            sender.sendMessage("${ChatColor.RED}${user.getUsername()} is already exempt!")
        } else {
            LeaderboardsModule.exemptions.add(user.uuid)
            LeaderboardsModule.saveExemptions()

            sender.sendMessage("${ChatColor.GREEN}Added ${user.getUsername()} to exemptions!")
        }
    }

}
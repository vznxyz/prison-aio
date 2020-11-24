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

object ExemptionRemoveCommand {

    @Command(
        names = ["leaderboard exemption remove", "lb exemption remove", "leaderboard exempt remove", "lb exempt remove"],
        description = "Remove a leaderboard exemption",
        permission = Permissions.LEADERBOARDS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User) {
        if (!LeaderboardsModule.exemptions.contains(user.uuid)) {
            sender.sendMessage("${ChatColor.RED}${user.getUsername()} is not exempt!")
        } else {
            LeaderboardsModule.exemptions.remove(user.uuid)
            LeaderboardsModule.saveExemptions()

            sender.sendMessage("${ChatColor.GREEN}Removed ${user.getUsername()} from exemptions!")
        }
    }

}
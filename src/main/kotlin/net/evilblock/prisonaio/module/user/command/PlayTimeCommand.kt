/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PlayTimeCommand {

    @Command(
        names = ["playtime", "pt", "checkpt"],
        description = "Shows a player's time played",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") user: User) {
        val timePlayed = TimeUtil.formatIntoDetailedString((user.statistics.getLivePlayTime() / 1000.0).toInt())

        if (sender is Player && sender.uniqueId == user.uuid) {
            sender.sendMessage("${ChatColor.YELLOW}Your total playtime is ${ChatColor.LIGHT_PURPLE}${timePlayed}${ChatColor.YELLOW}.")
        } else {
            sender.sendMessage("${ChatColor.YELLOW}${user.getUsername()}'s total playtime is ${ChatColor.LIGHT_PURPLE}${timePlayed}${ChatColor.YELLOW}.")
        }
    }

}
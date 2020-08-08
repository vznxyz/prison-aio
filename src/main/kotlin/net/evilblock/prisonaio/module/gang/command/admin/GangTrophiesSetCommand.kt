/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GangTrophiesSetCommand {

    @Command(
        names = ["gang trophies set", "gangs trophies set"],
        description = "Set a gang's trophies",
        permission = Permissions.GANGS_TROPHIES_SET,
        async = true
    )
    @JvmStatic
    fun sender(sender: CommandSender, @Param(name = "gang") gang: Gang, @Param(name = "trophies") trophies: Int) {
        gang.setTrophies(trophies)
        gang.sendMessagesToMembers("${ChatColor.YELLOW}An administrator has set your gang's trophies to ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(trophies)}${ChatColor.YELLOW}.")
        sender.sendMessage("${ChatColor.GREEN}You set `${gang.name}`s trophies to ${ChatColor.BOLD}${trophies}${ChatColor.GREEN}.")
    }

}
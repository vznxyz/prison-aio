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

object GangTrophiesTakeCommand {

    @Command(
        names = ["gang trophies take", "gangs trophies take"],
        description = "Take trophies from a gang",
        permission = Permissions.GANGS_TROPHIES_TAKE,
        async = true
    )
    @JvmStatic
    fun sender(sender: CommandSender, @Param(name = "gang") gang: Gang, @Param(name = "trophies") trophies: Int) {
        gang.takeTrophies(trophies)
        gang.sendMessagesToMembers("${ChatColor.YELLOW}An administrator has taken ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(trophies)} ${ChatColor.YELLOW}trophies from your gang.")
        sender.sendMessage("${ChatColor.GREEN}You subtracted ${ChatColor.BOLD}${trophies} ${ChatColor.GREEN}trophies from `${gang.name}`.")
    }

}
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

object GangTrophiesGiveCommand {

    @Command(
        names = ["gang trophies give", "gangs trophies give"],
        description = "Give trophies to a gang",
        permission = Permissions.GANGS_TROPHIES_GIVE,
        async = true
    )
    @JvmStatic
    fun sender(sender: CommandSender, @Param(name = "gang") gang: Gang, @Param(name = "trophies") trophies: Int) {
        gang.giveTrophies(trophies)
        gang.sendMessagesToMembers("${ChatColor.YELLOW}An administrator has given your gang ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(trophies)} ${ChatColor.YELLOW}trophies.")
        sender.sendMessage("${ChatColor.GREEN}You added ${ChatColor.BOLD}${trophies} ${ChatColor.GREEN}trophies to `${gang.name}`.")
    }

}
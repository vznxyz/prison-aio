/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.gang.GangsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GangHelpCommand {

    private val HELP_MESSAGE = listOf(
        "${ChatColor.GRAY}${Constants.LONG_LINE}",
        "${ChatColor.RED}${ChatColor.BOLD}Gangs Help ${ChatColor.GRAY}- ${ChatColor.WHITE}Information on how to use gang commands",
        "${ChatColor.GRAY}${Constants.LONG_LINE}",
        "${ChatColor.RED}General Commands:",
        "${ChatColor.YELLOW}/gang help ${ChatColor.GRAY}- Displays this help page",
        "${ChatColor.YELLOW}/gang create ${ChatColor.GRAY}- Create a new gang",
        "${ChatColor.YELLOW}/gang home ${ChatColor.GRAY}- Teleport to one of your gangs",
        "${ChatColor.YELLOW}/gang homes ${ChatColor.GRAY}- Opens the Homes menu",
        "${ChatColor.YELLOW}/gang sethome ${ChatColor.GRAY}- Set the spawn point of your gang",
        "${ChatColor.YELLOW}/gang info ${ChatColor.GRAY}- Show information about your gang",
        "${ChatColor.YELLOW}/gang invite ${ChatColor.GRAY}- Invite a player to your gang",
        "${ChatColor.YELLOW}/gang revokeinvite ${ChatColor.GRAY}- Revoke a player's invitation",
        "${ChatColor.YELLOW}/gang join ${ChatColor.GRAY}- Join a gang you've been invited to",
        "${ChatColor.YELLOW}/gang kick ${ChatColor.GRAY}- Kick a player from your gang",
        "${ChatColor.YELLOW}/gang leave ${ChatColor.GRAY}- Leave a gang you've joined",
        "${ChatColor.YELLOW}/gang visit ${ChatColor.GRAY}- Visit another player's gang",
        "${ChatColor.YELLOW}/gang setannouncement ${ChatColor.GRAY}- Update your gang's announcement",
        "",
        "${ChatColor.RED}Other Help:",
        "${ChatColor.YELLOW}To use ${ChatColor.GREEN}gang chat${ChatColor.YELLOW}, prefix your message with the ${ChatColor.GRAY}'${ChatColor.GREEN}@${ChatColor.GRAY}' ${ChatColor.YELLOW}symbol.",
        "${ChatColor.YELLOW}Gangs are limited to ${ChatColor.GREEN}${GangsModule.getMaxMembers()} members${ChatColor.YELLOW}.",
        "${ChatColor.GRAY}${Constants.LONG_LINE}"
    )

    @Command(
        names = ["gang help", "gangs help"],
        description = "Information about gangs"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        HELP_MESSAGE.forEach(sender::sendMessage)
    }

}
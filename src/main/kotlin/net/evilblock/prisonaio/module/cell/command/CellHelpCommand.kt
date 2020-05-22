package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.cell.CellsModule
import net.evilblock.prisonaio.util.Constants
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CellHelpCommand {

    private val HELP_MESSAGE = listOf(
        "${ChatColor.GRAY}${Constants.LONG_LINE}",
        "${ChatColor.RED}${ChatColor.BOLD}Cells Help ${ChatColor.GRAY}- ${ChatColor.WHITE}Information on how to use cell commands",
        "${ChatColor.GRAY}${Constants.LONG_LINE}",
        "${ChatColor.RED}General Commands:",
        "${ChatColor.YELLOW}/cell help ${ChatColor.GRAY}- Displays this help page",
        "${ChatColor.YELLOW}/cell create ${ChatColor.GRAY}- Create a new cell",
        "${ChatColor.YELLOW}/cell home ${ChatColor.GRAY}- Teleport to one of your cells",
        "${ChatColor.YELLOW}/cell homes ${ChatColor.GRAY}- Opens the Homes menu",
        "${ChatColor.YELLOW}/cell sethome ${ChatColor.GRAY}- Set the spawn point of your cell",
        "${ChatColor.YELLOW}/cell info ${ChatColor.GRAY}- Show information about your cell",
        "${ChatColor.YELLOW}/cell invite ${ChatColor.GRAY}- Invite a player to your cell",
        "${ChatColor.YELLOW}/cell revokeinvite ${ChatColor.GRAY}- Revoke a player's invitation",
        "${ChatColor.YELLOW}/cell join ${ChatColor.GRAY}- Join a cell you've been invited to",
        "${ChatColor.YELLOW}/cell kick ${ChatColor.GRAY}- Kick a player from your cell",
        "${ChatColor.YELLOW}/cell leave ${ChatColor.GRAY}- Leave a cell you've been invited to",
        "${ChatColor.YELLOW}/cell visit ${ChatColor.GRAY}- Visit another player's cell",
        "${ChatColor.YELLOW}/cell setannouncement ${ChatColor.GRAY}- Update your cell's announcement",
        "",
        "${ChatColor.RED}Other Help:",
        "${ChatColor.YELLOW}To use ${ChatColor.GREEN}cell chat${ChatColor.YELLOW}, prefix your message with the ${ChatColor.GRAY}'${ChatColor.GREEN}@${ChatColor.GRAY}' ${ChatColor.YELLOW}symbol.",
        "${ChatColor.YELLOW}Cells are limited to ${ChatColor.GREEN}${CellsModule.getMaxMembers()} members${ChatColor.YELLOW}.",
        "${ChatColor.GRAY}${Constants.LONG_LINE}"
    )

    @Command(
        names = ["cell help", "cells help"],
        description = "Information about Cells"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        HELP_MESSAGE.forEach(sender::sendMessage)
    }

}
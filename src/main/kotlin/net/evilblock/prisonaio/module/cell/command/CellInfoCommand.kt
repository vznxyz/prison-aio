package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.util.Constants
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellInfoCommand {

    @Command(names = ["cell info", "cells info", "cell who", "cells who"], description = "Show information about the cell you're visiting")
    @JvmStatic
    fun execute(sender: Player, @Param(name = "cell", defaultValue = "self") cell: Cell) {
        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")

        val ownerName = Cubed.instance.uuidCache.name(cell.owner)
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}$ownerName's Cell ${ChatColor.GRAY}[${ChatColor.WHITE}${cell.getActiveMembers().size}${ChatColor.GRAY}/${cell.getMembers().size}]")

        val memberNames = cell.getMembers().map { uuid ->
            val username = Cubed.instance.uuidCache.name(uuid)

            return@map if (Bukkit.getPlayer(uuid) == null) {
                "${ChatColor.RED}$username"
            } else {
                "${ChatColor.GREEN}$username"
            }
        }

        sender.sendMessage("${ChatColor.GRAY} Announcement: ${ChatColor.LIGHT_PURPLE}${cell.announcement}")
        sender.sendMessage("${ChatColor.GRAY} Members: ${ChatColor.WHITE}${memberNames.joinToString(separator = "${ChatColor.WHITE}, ")}")
        sender.sendMessage("${ChatColor.GRAY} Value: ${ChatColor.AQUA}$${ChatColor.GREEN}13.37T")

        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")
    }

}
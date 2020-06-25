/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.util.Constants
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellInfoCommand {

    @Command(
        names = ["cell info", "cells info", "cell who", "cells who"],
        description = "Show information about the cell you're visiting"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "cell", defaultValue = "self") cell: Cell) {
        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")

        val playersOnline = "${ChatColor.GREEN}${cell.getActiveMembers().size}${ChatColor.GRAY}/${cell.getMembers().size}"
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}${cell.name} ${ChatColor.GRAY}[$playersOnline]")

        val memberNames = cell.getMembers().map { uuid ->
            val username = Cubed.instance.uuidCache.name(uuid)

            val role = if (cell.isOwner(uuid)) {
                "**"
            } else {
                ""
            }

            return@map if (Bukkit.getPlayer(uuid) == null) {
                "${ChatColor.RED}$role$username"
            } else {
                "${ChatColor.GREEN}$role$username"
            }
        }

        sender.sendMessage("${ChatColor.GRAY} Announcement: ${ChatColor.LIGHT_PURPLE}${cell.announcement}")
        sender.sendMessage("${ChatColor.GRAY} Members: ${ChatColor.WHITE}${memberNames.joinToString(separator = "${ChatColor.WHITE}, ")}")
        sender.sendMessage("${ChatColor.GRAY} Value: ${ChatColor.RED}${NumberUtils.format(cell.cachedCellValue)}")

        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")
    }

}
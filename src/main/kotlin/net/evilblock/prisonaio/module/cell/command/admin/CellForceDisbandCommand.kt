/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CellForceDisbandCommand {

    @Command(
        names = ["cell admin force-disband", "cells admin force-disband"],
        description = "Force disband a cell",
        permission = Permissions.CELLS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "cell") cell: Cell) {
        cell.sendMessages("${ChatColor.YELLOW}The cell has been forcefully disbanded by an administrator.")

        for (member in cell.getMembers()) {
            CellHandler.updateJoinableCache(member, cell, false)
        }

        for (activePlayer in cell.getActivePlayers()) {
            CellHandler.updateVisitingCell(activePlayer, null)

            Tasks.sync {
                activePlayer.teleport(Bukkit.getWorlds()[0].spawnLocation)
            }
        }

        CellHandler.forgetCell(cell)
        RegionsModule.clearBlockCache(cell)

        sender.sendMessage("${ChatColor.GREEN}You have successfully disbanded `${cell.name}`.")
    }

}
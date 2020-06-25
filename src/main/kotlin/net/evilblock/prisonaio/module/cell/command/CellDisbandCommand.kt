/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellDisbandCommand {

    @Command(
        names = ["cell disband", "cells disband"],
        description = "Disband your cell",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val cell = CellHandler.getAssumedCell(player.uniqueId)
        if (cell == null) {
            player.sendMessage("${ChatColor.RED}You must be in a cell to disband it.")
            return
        }

        if (!cell.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You must be the owner of this cell to disband it.")
            return
        }

        cell.sendMessages("${ChatColor.YELLOW}The cell has been disbanded by the owner.")

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

        player.sendMessage("${ChatColor.GREEN}You have successfully disbanded your cell.")
    }

}
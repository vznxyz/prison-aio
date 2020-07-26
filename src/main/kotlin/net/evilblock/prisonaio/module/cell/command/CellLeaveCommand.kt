/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellLeaveCommand {

    @Command(
        names = ["cell leave", "cells leave"],
        description = "Leave a cell",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val visitingCell = CellHandler.getAssumedCell(player.uniqueId)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to leave it.")
            return
        }

        if (visitingCell.owner == player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Owners must disband their cell instead of leaving. Try `/cell disband`.")
            return
        }

        if (visitingCell.isActivePlayer(player)) {
            CellHandler.updateVisitingCell(player, null)

            Tasks.sync {
                player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            }
        }

        visitingCell.memberLeave(player.uniqueId)

        val ownerName = Cubed.instance.uuidCache.name(visitingCell.owner)
        player.sendMessage("${ChatColor.GREEN}Successfully left $ownerName's cell.")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellVisitCommand {

    @Command(
        names = ["cell visit", "cell v", "cells visit", "cells v"],
        description = "Visit another player's cell"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player", defaultValue = "self") cell: Cell) {
        if (CellHandler.getJoinableCells(player.uniqueId).contains(cell)) {
            player.sendMessage("${ChatColor.RED}If you want to join your own cell, use the `/cell join` command.")
            return
        }

        // attemptJoinSession handles the ALLOW_VISITORS permission test so we don't have to
        CellHandler.attemptJoinSession(player, cell)
    }

}
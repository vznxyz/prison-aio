/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.listener

import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object CellChatListeners : Listener {

    @EventHandler
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        if (event.message.startsWith("@")) {
            val joinableCells = CellHandler.getJoinableCells(event.player.uniqueId)
            if (joinableCells.size == 1) {
                handleMessage(event, joinableCells.first())
                return
            }

            val visitingCell = CellHandler.getVisitingCell(event.player)
            if (visitingCell != null) {
                handleMessage(event, visitingCell)
            }
        }
    }

    private fun handleMessage(event: AsyncPlayerChatEvent, cell: Cell) {
        event.isCancelled = true

        val message = StringBuilder()
            .append("${ChatColor.GRAY}[${ChatColor.YELLOW}${cell.name}${ChatColor.GRAY}] ")
            .append("${ChatColor.GREEN}${event.player.name}${ChatColor.GRAY}: ${event.message.drop(1)}")
            .toString()

        cell.sendMessages(message)
    }

}
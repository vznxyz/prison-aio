/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object GangChatListeners : Listener {

    @EventHandler
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        if (event.message.startsWith("@")) {
            val joinableCells = GangHandler.getAccessibleGangs(event.player.uniqueId)
            if (joinableCells.size == 1) {
                handleMessage(event, joinableCells.first())
                return
            }

            val visitingCell = GangHandler.getVisitingGang(event.player)
            if (visitingCell != null) {
                handleMessage(event, visitingCell)
            }
        }
    }

    private fun handleMessage(event: AsyncPlayerChatEvent, gang: Gang) {
        event.isCancelled = true

        val message = StringBuilder()
            .append("${ChatColor.GRAY}[${ChatColor.YELLOW}${gang.name}${ChatColor.GRAY}] ")
            .append("${ChatColor.GREEN}${event.player.name}${ChatColor.GRAY}: ${event.message.drop(1)}")
            .toString()

        gang.sendMessagesToAll(message)
    }

}
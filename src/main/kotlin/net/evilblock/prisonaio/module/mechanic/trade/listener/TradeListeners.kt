/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade.listener

import net.evilblock.prisonaio.module.mechanic.trade.TradeHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent

object TradeListeners : Listener {

    /**
     * Handles hiding player chat messages while trading.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        event.recipients.removeIf { TradeHandler.getActiveTrade(event.player) != null }
    }

    /**
     * Handles cancelling any active trade the player was involved in.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        TradeHandler.getActiveTrade(event.player)?.let { trade ->
            TradeHandler.forgetActiveTrade(trade)

            trade.cancel()
            trade.sendMessage("${Formats.formatPlayer(event.player)} ${ChatColor.RED}logged out, so the trade has been cancelled!")
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mechanic.trade.task.TradeRequestExpirationTask
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object TradeHandler {

    @JvmStatic
    var disabled: Boolean = false

    private val activeTrades: MutableList<Trade> = arrayListOf()

    /**
     * A map of player -> active trade.
     */
    private val playerTrades: MutableMap<UUID, Trade> = ConcurrentHashMap()

    /**
     * A map of player -> pending requests from other players.
     */
    private val pendingRequests: MutableMap<UUID, MutableSet<TradeRequest>> = ConcurrentHashMap()

    fun initialLoad() {
        Tasks.asyncTimer(TradeRequestExpirationTask, 10L, 10L)
    }

    fun getActiveTrade(player: Player): Trade? {
        return playerTrades[player.uniqueId]
    }

    fun trackActiveTrade(trade: Trade) {
        activeTrades.add(trade)
        playerTrades[trade.sender.uniqueId] = trade
        playerTrades[trade.target.uniqueId] = trade
    }

    fun forgetActiveTrade(trade: Trade) {
        activeTrades.remove(trade)
        playerTrades.remove(trade.sender.uniqueId)
        playerTrades.remove(trade.target.uniqueId)
    }

    fun cancelActiveTrades() {
        for (trade in activeTrades) {
            trade.cancel()
        }
    }

    fun getPendingRequests(): List<TradeRequest> {
        return arrayListOf<TradeRequest>().also {
            for (list in pendingRequests.values) {
                it.addAll(list)
            }
        }
    }

    fun hasPendingRequestFrom(sender: Player, target: Player): Boolean {
        if (pendingRequests.containsKey(target.uniqueId)) {
            return pendingRequests[target.uniqueId]!!.any { it.sender.uniqueId == sender.uniqueId }
        }

        return false
    }

    fun getPendingRequest(sender: Player, target: Player): TradeRequest? {
        return pendingRequests[target.uniqueId]?.firstOrNull { it.sender.uniqueId == sender.uniqueId }
    }

    fun trackPendingRequest(request: TradeRequest) {
        if (pendingRequests.containsKey(request.target.uniqueId)) {
            pendingRequests[request.target.uniqueId]!!.add(request)
        } else {
            pendingRequests[request.target.uniqueId] = hashSetOf(request)
        }
    }

    fun forgetPendingRequest(sender: Player, target: Player) {
        if (pendingRequests.containsKey(target.uniqueId)) {
            pendingRequests[target.uniqueId]!!.removeIf { it.sender.uniqueId == sender.uniqueId }
        }
    }

}
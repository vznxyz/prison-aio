/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.display

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BidHistoryDisplay : Listener {

    private val displayedBidHistory: MutableMap<UUID, MutableSet<UUID>> = ConcurrentHashMap()

    @JvmStatic
    fun isDisplayingBidHistory(player: Player, listing: GrandExchangeListing): Boolean {
        return displayedBidHistory.containsKey(player.uniqueId) && displayedBidHistory[player.uniqueId]!!.contains(listing.id)
    }

    @JvmStatic
    fun toggleBidHistoryDisplay(player: Player, listing: GrandExchangeListing) {
        if (displayedBidHistory.containsKey(player.uniqueId)) {
            val set = displayedBidHistory[player.uniqueId]!!
            if (set.contains(listing.id)) {
                set.remove(listing.id)
            } else {
                set.add(listing.id)
            }
        } else {
            displayedBidHistory[player.uniqueId] = ConcurrentHashMap.newKeySet<UUID>().also { it.add(listing.id) }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        displayedBidHistory.remove(event.player.uniqueId)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

object UserPerksListeners : Listener {

    /**
     * Applies the player's shop multiplier when selling to shops.
     */
    @EventHandler
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        val user = UserHandler.getUser(event.player.uniqueId)
        event.multiplier = user.perks.getSalesMultiplier(event.player).coerceAtLeast(1.0)
    }

    /**
     * Preserves game-mode, allow-flight, and is-flying states when switching worlds.
     */
    @EventHandler
    fun onPlayerChangedWorldEvent(event: PlayerChangedWorldEvent) {
        val gameMode = event.player.gameMode
        val allowFlight = event.player.allowFlight
        val isFlying = event.player.isFlying

        Tasks.delayed(1L) {
            event.player.gameMode = gameMode
            event.player.allowFlight = allowFlight
            event.player.isFlying = isFlying
            event.player.updateInventory()
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier.listener

import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object GlobalMultiplierListeners : Listener {

    /**
     * Applies the global shop multiplier when selling to shops.
     */
    @EventHandler
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        val globalMultiplier = GlobalMultiplierHandler.getActiveMultiplier()
        if (globalMultiplier != null) {
            event.multiplier = event.multiplier + globalMultiplier.multiplier
        }
    }

}
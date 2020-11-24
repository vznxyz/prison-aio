/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.listener

import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.event.DetermineShopEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MineShopListeners : Listener {

    /**
     * Handles determining which shop a player should sell to when standing nearby a [Mine] region.
     */
    @EventHandler
    fun onDetermineShopEvent(event: DetermineShopEvent) {
        for (mine in MineHandler.getMines()) {
            if (mine.isNearbyMine(event.player)) {
                val mineShop = ShopHandler.getShopById(mine.id)
                if (mineShop.isPresent) {
                    if (mineShop.get().hasAccess(event.player)) {
                        event.shop = mineShop.get()
                    }
                }

                break
            }
        }
    }

}
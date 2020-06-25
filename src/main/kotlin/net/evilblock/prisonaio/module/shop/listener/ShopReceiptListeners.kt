/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.listener

import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object ShopReceiptListeners : Listener {

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        ShopHandler.forgetReceipts(event.player)
    }

}
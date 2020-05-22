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
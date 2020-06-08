package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object UserPerksListeners : Listener {

    @EventHandler
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        val user = UserHandler.getUser(event.player.uniqueId)
        event.multiplier = user.perks.getSalesMultiplier(event.player).coerceAtLeast(1.0)
    }

}
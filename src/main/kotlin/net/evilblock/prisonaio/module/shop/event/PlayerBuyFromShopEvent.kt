/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PlayerBuyFromShopEvent(
    val player: Player,
    val shop: Shop,
    val items: List<ShopReceiptItem>
) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    fun getBuyCost(): Double {
        return items.sumByDouble { it.getBuyCost() }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.event

import net.evilblock.cubed.plugin.PluginEvent
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.math.BigDecimal
import java.math.BigInteger

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

    fun getCost(): Number {
        var price = BigDecimal(0.0)

        for (item in items) {
            price += BigDecimal(item.getBuyCost().toDouble())
        }

        return price
    }

}
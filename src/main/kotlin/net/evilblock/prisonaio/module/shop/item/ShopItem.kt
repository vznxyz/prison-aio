/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.item

import org.bukkit.inventory.ItemStack

data class ShopItem(
    /**
     * This shop item's actual item
     */
    val itemStack: ItemStack,
    /**
     * This shop item's amount
     */
    val amount: Int,
    /**
     * The price per unit the shop buys for
     */
    var buyPricePerUnit: Double = 0.0,
    /**
     * The price per unit the shop sells for
     */
    var sellPricePerUnit: Double = 0.0,
    /**
     * The order of this shop item
     */
    var order: Int = 0
) {

    /**
     * If the shop is buying this item
     */
    fun isBuying(): Boolean {
        return buyPricePerUnit != 0.0
    }

    /**
     * If the shop is selling this item
     */
    fun isSelling(): Boolean {
        return sellPricePerUnit != 0.0
    }

}
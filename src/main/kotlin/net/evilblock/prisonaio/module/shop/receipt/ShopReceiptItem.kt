/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.receipt

import net.evilblock.prisonaio.module.shop.item.ShopItem
import org.bukkit.inventory.ItemStack

data class ShopReceiptItem(val itemType: ShopItem, val item: ItemStack) {

    var buyPricePerUnit = itemType.sellPricePerUnit
    var sellPricePerUnit = itemType.buyPricePerUnit
    var multiplier: Double = 1.0

    /**
     * The amount the player is selling for.
     */
    fun getSellCost(): Double {
        return (item.amount * sellPricePerUnit) * multiplier
    }

    /**
     * The amount the player is buying for.
     */
    fun getBuyCost(): Double {
        return (item.amount * buyPricePerUnit) * multiplier
    }

}
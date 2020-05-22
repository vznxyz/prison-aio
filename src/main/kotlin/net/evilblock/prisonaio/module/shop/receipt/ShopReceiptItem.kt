package net.evilblock.prisonaio.module.shop.receipt

import net.evilblock.prisonaio.module.shop.item.ShopItem
import org.bukkit.inventory.ItemStack

data class ShopReceiptItem(val itemType: ShopItem, val item: ItemStack) {

    var buyPricePerUnit = itemType.buyPricePerUnit
    var sellPricePerUnit = itemType.sellPricePerUnit
    var multiplier: Double = 1.0

    /**
     * The amount that the player sells this shop item for.
     */
    fun getSellCost(): Double {
        return (item.amount * buyPricePerUnit) * multiplier
    }

    /**
     * The amount it costs the player to buy this shop item.
     */
    fun getBuyCost(): Double {
        return (item.amount * sellPricePerUnit) * multiplier
    }

}
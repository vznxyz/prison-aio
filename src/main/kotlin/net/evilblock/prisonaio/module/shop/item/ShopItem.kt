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
     * The price per unit when the shop is selling to a player
     */
    var sellPricePerUnit: Double = 0.0,
    /**
     * The price per unit when the shop is buying from a player
     */
    var buyPricePerUnit: Double = 0.0,
    /**
     * If this shop item is being sold by the shop
     */
    var selling: Boolean = false,
    /**
     * If this shop item is being bought by the shop
     */
    var buying: Boolean = false
)
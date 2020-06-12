package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.shop.event.PlayerBuyFromShopEvent
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Shop(val id: String) {

    var name: String = id
    val items: HashSet<ShopItem> = hashSetOf()
    var priority: Int = 0

    fun hasAccess(player: Player): Boolean {
        return player.hasPermission("prisonaio.shops.${id.toLowerCase()}")
    }

    fun buyItems(player: Player, items: Set<ShopReceiptItem>): ShopReceipt {
        if (this.items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, type = ShopReceiptType.BUY)
        }

        val itemsBought = items.filter { this.items.contains(it.itemType) }
        if (itemsBought.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, type = ShopReceiptType.BUY)
        }

        val buyEvent = PlayerBuyFromShopEvent(
            player = player,
            shop = this,
            items = itemsBought
        )

        Bukkit.getPluginManager().callEvent(buyEvent)

        if (buyEvent.isCancelled) {
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, type = ShopReceiptType.BUY)
        }

        if (buyEvent.items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, type = ShopReceiptType.BUY)
        }

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            items = buyEvent.items,
            type = ShopReceiptType.BUY,
            multiplier = 1.0,
            finalCost = buyEvent.items.sumByDouble { it.getBuyCost() }
        )

        shopReceipt.sendCompact(player)
        ShopHandler.trackReceipt(player, shopReceipt)

        return shopReceipt
    }

    fun sellItems(player: Player, selling: List<ItemStack>, autoSell: Boolean = false): ShopReceipt {
        if (items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, type = ShopReceiptType.SELL)
        }

        val itemsSold = arrayListOf<ShopReceiptItem>()
        for (item in selling) {
            val matchingShopItem = items.filter { it.buying }.firstOrNull { item.isSimilar(it.itemStack) }
            if (matchingShopItem != null) {
                itemsSold.add(ShopReceiptItem(matchingShopItem, item))
            }
        }

        val sellEvent = PlayerSellToShopEvent(
            player = player,
            shop = this,
            items = itemsSold,
            multiplier = 1.0,
            autoSell = autoSell
        )

        Bukkit.getPluginManager().callEvent(sellEvent)

        if (sellEvent.isCancelled) {
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, type = ShopReceiptType.SELL)
        }

        if (sellEvent.items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, type = ShopReceiptType.SELL)
        }

        if (itemsSold.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, type = ShopReceiptType.SELL)
        }

        val finalCost = itemsSold.sumByDouble { it.getSellCost() } * sellEvent.multiplier
        if (finalCost <= 0) {
            return ShopReceipt(result = TransactionResult.FREE_SELL, shop = this, type = ShopReceiptType.SELL)
        }

        VaultHook.useEconomyAndReturn { economy -> economy.depositPlayer(player, finalCost) }

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            type = ShopReceiptType.SELL,
            items = itemsSold,
            multiplier = sellEvent.multiplier,
            finalCost = finalCost
        )

        if (!autoSell) {
            shopReceipt.sendCompact(player)
            ShopHandler.trackReceipt(player, shopReceipt)
        }

        return shopReceipt
    }

}
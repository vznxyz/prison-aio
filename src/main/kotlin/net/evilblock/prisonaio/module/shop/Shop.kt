package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.shop.event.PlayerBuyFromShopEvent
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.shop.exception.ShopTransactionInterruptedException
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.module.user.UserHandler
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

    @Throws(ShopTransactionInterruptedException::class)
    fun buyItems(player: Player, items: Set<ShopReceiptItem>): ShopReceipt {
        if (this.items.isEmpty()) {
            throw ShopTransactionInterruptedException(reason = ShopTransactionInterruptedException.InterruptReason.SHOP_EMPTY)
        }

        val itemsBought = items.filter { this.items.contains(it.itemType) }

        if (itemsBought.isEmpty()) {
            throw ShopTransactionInterruptedException(reason = ShopTransactionInterruptedException.InterruptReason.NO_ITEMS)
        }

        val buyEvent = PlayerBuyFromShopEvent(
            player = player,
            shop = this,
            items = itemsBought
        )

        Bukkit.getPluginManager().callEvent(buyEvent)

        if (buyEvent.isCancelled) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.CANCELLED_PLUGIN)
        }

        if (buyEvent.items.isEmpty()) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.NO_ITEMS)
        }

        val shopReceipt = ShopReceipt(
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

    @Throws(ShopTransactionInterruptedException::class)
    fun sellItems(player: Player, selling: List<ItemStack>, autoSell: Boolean = false): ShopReceipt {
        if (items.isEmpty()) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.SHOP_EMPTY)
        }

        val itemsSold = arrayListOf<ShopReceiptItem>()
        for (item in selling) {
            for (shopItem in items.filter { it.buying }) {
                if (item.isSimilar(shopItem.itemStack)) {
                    itemsSold.add(ShopReceiptItem(shopItem, item))
                }
            }
        }

        val user = UserHandler.getUser(player.uniqueId)

        // determine sales multiplier
        val multiplier = user.perks.getSalesMultiplier(player).coerceAtLeast(1.0)

        val sellEvent = PlayerSellToShopEvent(
            player = player,
            shop = this,
            items = itemsSold,
            multiplier = multiplier
        )

        Bukkit.getPluginManager().callEvent(sellEvent)

        if (sellEvent.isCancelled) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.CANCELLED_PLUGIN)
        }

        if (sellEvent.items.isEmpty()) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.SHOP_EMPTY)
        }

        // handle having no items to sell
        if (itemsSold.isEmpty()) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.NO_ITEMS)
        }

        // calculate the cost
        val finalCost = itemsSold.sumByDouble { it.getSellCost() } * sellEvent.multiplier

        // can't sell for a negative price
        if (finalCost <= 0) {
            throw ShopTransactionInterruptedException(ShopTransactionInterruptedException.InterruptReason.FREE_SELL)
        }

        // remove all of the items from the player's inventory
        for (itemSold in itemsSold) {
            player.inventory.remove(itemSold.item)
        }

        // send inventory updates
        player.updateInventory()

        // payout the cost to the player
        VaultHook.useEconomyAndReturn { economy -> economy.depositPlayer(player, finalCost) }

        val shopReceipt = ShopReceipt(
            shop = this,
            type = ShopReceiptType.SELL,
            items = itemsSold,
            multiplier = multiplier,
            finalCost = finalCost
        )

        if (!autoSell) {
            shopReceipt.sendCompact(player)
            ShopHandler.trackReceipt(player, shopReceipt)
        }

        return shopReceipt
    }

}
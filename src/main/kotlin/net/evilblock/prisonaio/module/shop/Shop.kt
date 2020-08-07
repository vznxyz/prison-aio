/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.menu.template.menu.TemplateMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.shop.event.PlayerBuyFromShopEvent
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.template.ShopMenuTemplate
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import net.evilblock.prisonaio.util.economy.Currency
import net.evilblock.prisonaio.util.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Shop(val id: String) {

    var name: String = id
    val items: HashSet<ShopItem> = hashSetOf()
    var priority: Int = 0
    var menuTemplate: ShopMenuTemplate? = null
    var currency: Currency.Type = Currency.Type.MONEY

    fun init() {
        menuTemplate?.shop = this

        // fix null currency (field is new)
        if (currency == null) {
            currency = Currency.Type.MONEY
        }
    }

    fun hasAccess(player: Player): Boolean {
        return player.hasPermission("prisonaio.shops.${id.toLowerCase()}")
    }

    fun syncItemsOrder() {
        val items = items.sortedBy { it.order }
        for ((index, item) in items.withIndex()) {
            item.order = index
        }
    }

    fun buyItems(player: Player, buying: Set<ShopReceiptItem>): ShopReceipt {
        if (this.items.none { it.isSelling() }) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, receiptType = ShopReceiptType.BUY)
        }

        val itemsBought = buying.filter { this.items.contains(it.itemType) }

        val buyEvent = PlayerBuyFromShopEvent(
            player = player,
            shop = this,
            items = itemsBought
        )

        Bukkit.getPluginManager().callEvent(buyEvent)

        if (buyEvent.isCancelled) {
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, receiptType = ShopReceiptType.BUY)
        }

        if (itemsBought.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, receiptType = ShopReceiptType.BUY)
        }

        val finalCost = itemsBought.sumByDouble { it.getBuyCost().toDouble() }
        if (finalCost <= 0) {
            return ShopReceipt(result = TransactionResult.FREE_BUY, shop = this, receiptType = ShopReceiptType.BUY)
        }

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            items = itemsBought,
            receiptType = ShopReceiptType.BUY,
            multiplier = 1.0,
            finalCost = finalCost
        )

        if (!shopReceipt.currency.has(player, shopReceipt.finalCost)) {
            return ShopReceipt(result = TransactionResult.CANNOT_AFFORD, shop = this, receiptType = ShopReceiptType.BUY)
        }

        currency.take(player, finalCost)

        val splitItems = arrayListOf<ItemStack>()
        for (item in shopReceipt.items) {
            if (item.item.amount > item.item.type.maxStackSize) {
                var remaining = item.item.amount
                while (remaining > item.item.type.maxStackSize) {
                    remaining -= item.item.type.maxStackSize
                    splitItems.add(ItemBuilder.copyOf(item.item).amount(item.item.type.maxStackSize).build())
                }

                if (remaining > 0) {
                    splitItems.add(ItemBuilder.copyOf(item.item).amount(remaining).build())
                }
            } else {
                splitItems.add(item.item)
            }
        }

        for (item in player.inventory.addItem(*splitItems.toTypedArray())) {
            player.location.world.dropItem(player.location, item.value)
        }

        player.updateInventory()

        shopReceipt.sendCompact(player)

        return shopReceipt
    }

    fun sellItems(player: Player, selling: Collection<ItemStack>, autoSell: Boolean = false): ShopReceipt {
        if (items.none { it.isBuying() }) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, receiptType = ShopReceiptType.SELL)
        }

        val itemsSold = arrayListOf<ShopReceiptItem>()
        for (item in selling) {
            val matchingShopItem = items.filter { it.isBuying() }.firstOrNull { item.isSimilar(it.itemStack) }
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
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, receiptType = ShopReceiptType.SELL)
        }

        if (itemsSold.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, receiptType = ShopReceiptType.SELL)
        }

        val finalCost = itemsSold.sumByDouble { it.getSellCost().toDouble() } * sellEvent.multiplier
        if (finalCost <= 0) {
            return ShopReceipt(result = TransactionResult.FREE_SELL, shop = this, receiptType = ShopReceiptType.SELL)
        }

        VaultHook.useEconomyAndReturn { economy -> economy.depositPlayer(player, finalCost) }

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            receiptType = ShopReceiptType.SELL,
            items = itemsSold,
            multiplier = sellEvent.multiplier,
            finalCost = finalCost
        )

        if (!autoSell) {
            ShopHandler.trackReceipt(player, shopReceipt)
        }

        return shopReceipt
    }

    fun openMenu(player: Player) {
        if (menuTemplate == null) {
            player.sendMessage("${ChatColor.RED}Unable to open the $name ${ChatColor.RED}shop because it has no template!")
            return
        }

        if (items.none { it.isSelling() }) {
            player.sendMessage("${ChatColor.RED}The $name ${ChatColor.RED}shop is not selling any items!")
            return
        }

        TemplateMenu(menuTemplate!!).openMenu(player)
    }

}
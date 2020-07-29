/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.menu.template.menu.TemplateMenu
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.shop.event.PlayerBuyFromShopEvent
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.template.ShopMenuTemplate
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Shop(val id: String) {

    var name: String = id
    val items: HashSet<ShopItem> = hashSetOf()
    var priority: Int = 0
    var menuTemplate: ShopMenuTemplate? = null

    fun init() {
        menuTemplate?.shop = this
    }

    fun hasAccess(player: Player): Boolean {
        return player.hasPermission("prisonaio.shops.${id.toLowerCase()}")
    }

    fun buyItems(player: Player, items: Set<ShopReceiptItem>): ShopReceipt {
        if (this.items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, receiptType = ShopReceiptType.BUY)
        }

        val itemsBought = items.filter { this.items.contains(it.itemType) }
        if (itemsBought.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, receiptType = ShopReceiptType.BUY)
        }

        val buyEvent = PlayerBuyFromShopEvent(
            player = player,
            shop = this,
            items = itemsBought
        )

        Bukkit.getPluginManager().callEvent(buyEvent)

        if (buyEvent.isCancelled) {
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, receiptType = ShopReceiptType.BUY)
        }

        if (buyEvent.items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, receiptType = ShopReceiptType.BUY)
        }

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            items = buyEvent.items,
            receiptType = ShopReceiptType.BUY,
            multiplier = 1.0,
            finalCost = buyEvent.items.sumByDouble { it.getBuyCost() }
        )

        ShopHandler.trackReceipt(player, shopReceipt)

        return shopReceipt
    }

    fun sellItems(player: Player, selling: Collection<ItemStack>, autoSell: Boolean = false): ShopReceipt {
        if (items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, receiptType = ShopReceiptType.SELL)
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
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, receiptType = ShopReceiptType.SELL)
        }

        if (sellEvent.items.isEmpty()) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, receiptType = ShopReceiptType.SELL)
        }

        if (itemsSold.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, receiptType = ShopReceiptType.SELL)
        }

        val finalCost = itemsSold.sumByDouble { it.getSellCost() } * sellEvent.multiplier
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

        if (items.none { it.selling }) {
            player.sendMessage("${ChatColor.RED}The $name ${ChatColor.RED}shop is not selling any items!")
            return
        }

        TemplateMenu(menuTemplate!!).openMenu(player)
    }

}
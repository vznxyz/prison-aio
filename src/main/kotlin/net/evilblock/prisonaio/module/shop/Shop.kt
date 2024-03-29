/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.menu.template.menu.TemplateMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.shop.event.PlayerBuyFromShopEvent
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.template.ShopMenuTemplate
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

class Shop(var id: String) {

    var name: String = id
    val items: MutableSet<ShopItem> = ConcurrentHashMap.newKeySet()
    var priority: Int = 0
    var menuTemplate: ShopMenuTemplate? = null
    var currency: Currency.Type = Currency.Type.MONEY

    fun init() {
        menuTemplate?.shop = this

        syncItemsOrder()

        for (item in items) {
            if (item.commands == null) {
                item.commands = arrayListOf()
            }

            if (item.purchaseTimestamps == null) {
                item.purchaseTimestamps = ConcurrentHashMap()
            }
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

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            items = itemsBought,
            receiptType = ShopReceiptType.BUY,
            multiplier = 1.0,
            finalCost = buyEvent.getCost()
        )

        if (!shopReceipt.currency.has(player.uniqueId, shopReceipt.finalCost)) {
            return ShopReceipt(result = TransactionResult.CANNOT_AFFORD, shop = this, receiptType = ShopReceiptType.BUY, finalCost = shopReceipt.finalCost)
        }

        currency.take(player.uniqueId, shopReceipt.finalCost)

        val splitItems = arrayListOf<ItemStack>()
        for (item in shopReceipt.items) {
            if (item.itemType.commands.isNotEmpty()) {
                Tasks.sync {
                    for (command in item.itemType.commands) {
                        val translatedCommand = command
                            .replace("{playerName}", player.name)
                            .replace("{playerUuid}", player.uniqueId.toString())
                            .replace("{shopItemName}", ItemUtils.getChatName(item.itemType.itemStack))
                            .replace("{shopItemNameRaw}", ItemUtils.getName(item.itemType.itemStack))

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), translatedCommand)
                    }
                }
            }

            if (!item.itemType.giveItem) {
                continue
            }

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

        Tasks.sync {
            val droppedItems = player.inventory.addItem(*splitItems.toTypedArray())
//            if (droppedItems.isNotEmpty()) {
//                val region = RegionHandler.findRegion(player.location)
//                if (region is BitmaskRegion && !region.hasBitmask(RegionBitmask.SAFE_ZONE)) {
//                    for (item in droppedItems) {
//                        player.location.world.dropItem(player.location, item.value)
//                    }
//                }
//            }

            player.updateInventory()
        }

        shopReceipt.sendCompact(player)

        return shopReceipt
    }

    fun sellItems(player: Player, selling: Collection<ItemStack>, autoSell: Boolean = false): ShopReceipt {
        if (items.none { it.isBuying() }) {
            return ShopReceipt(result = TransactionResult.SHOP_EMPTY, shop = this, receiptType = ShopReceiptType.SELL, finalCost = 0)
        }

        val itemsSold = arrayListOf<ShopReceiptItem>()
        for (item in selling) {
            val matchingShopItem = items.filter { it.isBuying() }.firstOrNull { item.isSimilar(it.itemStack) }
            if (matchingShopItem != null) {
                itemsSold.add(ShopReceiptItem(matchingShopItem, item))
            }
        }

        val user = UserHandler.getUser(player.uniqueId)
        val multiplier = user.perks.getSalesMultiplier(player)

        val sellEvent = PlayerSellToShopEvent(
            player = player,
            shop = this,
            items = itemsSold,
            multiplier = multiplier,
            autoSell = autoSell
        )

        Bukkit.getPluginManager().callEvent(sellEvent)

        if (sellEvent.isCancelled) {
            return ShopReceipt(result = TransactionResult.CANCELLED_PLUGIN, shop = this, receiptType = ShopReceiptType.SELL)
        }

        if (itemsSold.isEmpty()) {
            return ShopReceipt(result = TransactionResult.NO_ITEMS, shop = this, receiptType = ShopReceiptType.SELL)
        }

        val shopReceipt = ShopReceipt(
            result = TransactionResult.SUCCESS,
            shop = this,
            receiptType = ShopReceiptType.SELL,
            items = itemsSold,
            multiplier = sellEvent.multiplier,
            finalCost = sellEvent.getCost()
        )

        user.addMoneyBalance(shopReceipt.finalCost as BigDecimal)

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

        TemplateMenu(template = menuTemplate!!, autoUpdate = true, autoUpdateInterval = 500L).openMenu(player)
    }

    override fun equals(other: Any?): Boolean {
        return other is Shop && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
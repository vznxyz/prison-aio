/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.shop.event.DetermineShopEvent
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptType
import net.evilblock.prisonaio.module.shop.service.ShopCooldownsExpiryService
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

object ShopHandler: PluginHandler() {

    private val shopsMap: MutableMap<String, Shop> = hashMapOf()
    private val receipts = hashMapOf<UUID, MutableSet<ShopReceipt>>()

    override fun getModule(): PluginModule {
        return ShopsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "shops.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Shop>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Shop>

                for (shop in list) {
                    shop.init()
                    shopsMap[shop.id.toLowerCase()] = shop
                }
            }
        }

        ServiceRegistry.register(ShopCooldownsExpiryService, 20L)
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(shopsMap.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getAccessibleShops(player: Player): List<Shop> {
        return shopsMap.values
            .filter { it.hasAccess(player) }
            .sortedBy { it.priority }
            .reversed()
    }

    fun getDefaultShop(): Optional<Shop> {
        return getShopById(getModule().config.getString("default-shop", "main"))
    }

    fun sellItems(player: Player, items: MutableCollection<ItemStack>, autoSell: Boolean) {
        val determineShopEvent = DetermineShopEvent(player)
        determineShopEvent.call()

        if (determineShopEvent.shop != null) {
            val receipt = determineShopEvent.shop!!.sellItems(player, items, autoSell)
            if (receipt.result == TransactionResult.SUCCESS) {
                items.removeAll(receipt.items.map { it.item })
                return
            }
        }

        val accessibleShops = getAccessibleShops(player)
        if (accessibleShops.isEmpty()) {
            if (!autoSell) {
                player.sendMessage("${ChatColor.RED}Couldn't find any shops to sell to.")
            }
            return
        }

        for (shop in accessibleShops) {
            val receipt = shop.sellItems(player, items, true)
            if (receipt.result == TransactionResult.SUCCESS) {
                items.removeAll(receipt.items.map { it.item })

                if (items.isEmpty()) {
                    break
                }
            }
        }
    }

    fun sellInventory(player: Player, autoSell: Boolean) {
        val items = player.inventory.storageContents.filterNotNull().toMutableList()

        Tasks.async {
            val backpacks = BackpackHandler.findBackpacksInInventory(player)
            val accessibleShops = getAccessibleShops(player)
            val sendMessages = UserHandler.getUser(player.uniqueId).settings.getSettingOption(UserSetting.SHOP_NOTIFICATIONS).getValue() as Boolean
            var soldAnything = false
            var firstOfChain = true

            val determineShopEvent = DetermineShopEvent(player)
            determineShopEvent.call()

            if (determineShopEvent.shop != null) {
                val receipt = determineShopEvent.shop!!.sellItems(player, items, false)
                if (receipt.result == TransactionResult.SUCCESS) {
                    Tasks.sync {
                        for (receiptItem in receipt.items) {
                            player.inventory.removeItem(receiptItem.item)
                        }

                        player.updateInventory()
                    }

                    if (sendMessages) {
                        receipt.sendCompact(player, true)
                    }

                    sellBackpacksContents(player, backpacks, accessibleShops, sendMessages, false)
                    return@async
                } else if (receipt.result == TransactionResult.NO_ITEMS) {
                    if (sellBackpacksContents(player, backpacks, accessibleShops, sendMessages, true)) {
                        return@async
                    }
                }
            }

            if (accessibleShops.isEmpty()) {
                player.sendMessage("${ChatColor.RED}Couldn't find any shops to sell to.")
                return@async
            }

            if (sellBackpacksContents(player, backpacks, accessibleShops, sendMessages, firstOfChain)) {
                soldAnything = true
                firstOfChain = false
            }

            for (shop in accessibleShops) {
                val receipt = shop.sellItems(player, items, autoSell)
                if (receipt.result == TransactionResult.SUCCESS) {
                    soldAnything = true

                    items.removeAll(receipt.items.map { it.item })

                    Tasks.sync {
                        for (receiptItem in receipt.items) {
                            player.inventory.removeItem(receiptItem.item)
                        }
                    }

                    if (sendMessages) {
                        receipt.sendCompact(player, firstOfChain)
                    }

                    firstOfChain = false

                    if (items.isEmpty()) {
                        break
                    }
                }
            }

            if (soldAnything) {
                player.updateInventory()
            } else {
                player.sendMessage("${ChatColor.RED}${TransactionResult.NO_ITEMS.getMessage(ShopReceiptType.SELL)}!")
            }
        }
    }

    private fun sellBackpacksContents(player: Player, backpacks: Map<ItemStack, Backpack>, shops: List<Shop>, sendMessages: Boolean, _firstOfChain: Boolean): Boolean {
        var soldAnything = false
        var firstOfChain = _firstOfChain

        for (shop in shops) {
            for ((backpackItem, backpack) in backpacks) {
                val receipt = shop.sellItems(player, backpack.contents, false)
                if (receipt.result == TransactionResult.SUCCESS) {
                    soldAnything = true

                    for (soldItem in receipt.items) {
                        backpack.removeItem(soldItem.item)
                    }

                    if (sendMessages) {
                        receipt.sendCompact(player, firstOfChain)
                    }

                    firstOfChain = false
                }
            }
        }

        return soldAnything
    }

    fun getShopById(id: String): Optional<Shop> {
        if (shopsMap.containsKey(id.toLowerCase())) {
            return Optional.of(shopsMap[id.toLowerCase()]!!)
        }
        return Optional.empty()
    }

    fun getShops(): Collection<Shop> {
        return shopsMap.values
    }

    fun trackShop(shop: Shop) {
        shopsMap[shop.id.toLowerCase()] = shop
    }

    fun forgetShop(shop: Shop): Boolean {
        return shopsMap.remove(shop.id.toLowerCase()) != null
    }

    fun getReceipts(): Map<UUID, MutableSet<ShopReceipt>> {
        return receipts
    }

    fun getReceiptById(player: Player, uuid: UUID): ShopReceipt? {
        if (!receipts.containsKey(player.uniqueId)) {
            return null
        }

        return receipts[player.uniqueId]!!.firstOrNull { it.uuid == uuid }
    }

    fun trackReceipt(player: Player, receipt: ShopReceipt) {
        if (!receipts.containsKey(player.uniqueId)) {
            receipts[player.uniqueId] = hashSetOf()
        }

        receipts[player.uniqueId]!!.add(receipt)
    }

    fun forgetReceipts(player: Player) {
        receipts.remove(player.uniqueId)
    }

}
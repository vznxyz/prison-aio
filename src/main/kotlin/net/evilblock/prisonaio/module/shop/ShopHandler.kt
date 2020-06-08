package net.evilblock.prisonaio.module.shop

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object ShopHandler: PluginHandler {

    private val shopsMap: MutableMap<String, Shop> = hashMapOf()
    private val receipts = hashMapOf<UUID, MutableSet<ShopReceipt>>()

    override fun getModule(): PluginModule {
        return ShopsModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "shops.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Shop>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Shop>

                for (shop in list) {
                    shopsMap[shop.id.toLowerCase()] = shop
                }
            }
        }
    }

    override fun saveData() {
        Files.write(Cubed.gson.toJson(shopsMap.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun sellItems(player: Player, items: MutableList<ItemStack>): List<ItemStack> {
        val accessibleShops = shopsMap.values.filter { it.hasAccess(player) }.sortedBy { it.priority }
        if (accessibleShops.isEmpty()) {
            player.sendMessage("${ChatColor.RED}Couldn't find any shops to sell to.")
            return emptyList()
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

        return items
    }

    fun sellInventory(player: Player, autoSell: Boolean) {
        val items = player.inventory.contents.filterNotNull().toMutableList()
        if (items.isEmpty()) {
            player.sendMessage("${ChatColor.RED}${TransactionResult.NO_ITEMS.defaultMessage}!")
            return
        }

        val accessibleShops = shopsMap.values.filter { it.hasAccess(player) }.sortedBy { it.priority }
        if (accessibleShops.isEmpty()) {
            player.sendMessage("${ChatColor.RED}Couldn't find any shops to sell to.")
            return
        }

        for (shop in accessibleShops) {
            val receipt = shop.sellItems(player, items, autoSell)
            if (receipt.result == TransactionResult.SUCCESS) {
                items.removeAll(receipt.items.map { it.item })

                for (receiptItem in receipt.items) {
                    player.inventory.remove(receiptItem.item)
                }

                if (items.isEmpty()) {
                    break
                }
            }
        }

        player.updateInventory()
    }

    fun getShopById(id: String): Optional<Shop> {
        if (shopsMap.containsKey(id.toLowerCase())) {
            return Optional.of(shopsMap[id.toLowerCase()]!!)
        }
        return Optional.empty()
    }

    fun getShops(): List<Shop> {
        return ArrayList(shopsMap.values)
    }

    fun trackShop(shop: Shop) {
        shopsMap[shop.id.toLowerCase()] = shop
    }

    fun forgetShop(shop: Shop): Boolean {
        return shopsMap.remove(shop.id.toLowerCase()) != null
    }

    fun getReceipts(): Map<UUID, MutableSet<ShopReceipt>> {
        return receipts.toMap()
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
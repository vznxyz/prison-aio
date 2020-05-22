package net.evilblock.prisonaio.module.shop

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.shop.exception.ShopTransactionInterruptedException
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
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

    @Throws(IllegalStateException::class, ShopTransactionInterruptedException::class)
    fun sellDrops(player: Player, drops: List<ItemStack>): List<ItemStack> {
        val dropsRemaining = drops.toMutableList()

        val accessibleShops = shopsMap.values.filter { it.hasAccess(player) }.sortedBy { it.priority }
        for (shop in accessibleShops) {
            try {
                val receipt = shop.sellItems(player, drops, true)
                for (receiptItem in receipt.items) {
                    dropsRemaining.remove(receiptItem.item)
                }
            } catch (e: ShopTransactionInterruptedException) {
                continue
            }
        }

        if (accessibleShops.isEmpty()) {
            throw IllegalStateException("Couldn't find a shop to sell to")
        }

        if (drops.size != dropsRemaining.size) {
            player.updateInventory()
        } else {
            throw IllegalStateException("No items could be sold")
        }

        return dropsRemaining
    }

    @Throws(IllegalStateException::class, ShopTransactionInterruptedException::class)
    fun sellInventory(player: Player, autoSell: Boolean) {
        var sold = false

        val accessibleShops = shopsMap.values.filter { it.hasAccess(player) }.sortedBy { it.priority }
        for (shop in accessibleShops) {
            try {
                val receipt = shop.sellItems(player, player.inventory.contents.filterNotNull().toList(), autoSell)
                for (receiptItem in receipt.items) {
                    player.inventory.remove(receiptItem.item)
                }

                sold = true
            } catch (e: ShopTransactionInterruptedException) {
                continue
            }
        }

        if (accessibleShops.isEmpty()) {
            throw IllegalStateException("Couldn't find a shop to sell to")
        }

        if (sold) {
            player.updateInventory()
        } else {
            throw IllegalStateException("No items could be sold")
        }
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
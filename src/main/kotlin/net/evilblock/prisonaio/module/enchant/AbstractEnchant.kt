/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant

import net.evilblock.cubed.util.bukkit.ColorUtil.toChatColor
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class AbstractEnchant(val id: String, val enchant: String, val maxLevel: Int) {

    abstract val iconColor: Color
    abstract val textColor: ChatColor

    val strippedEnchant: String
        get() = ChatColor.stripColor(enchant)

    fun readDescription(): String {
        return EnchantsModule.config.getString("$id.description")
    }

    fun sendMessage(player: Player, message: String) {
        if (!player.hasMetadata("ENCHANT_MSGS_DISABLED")) {
            player.sendMessage("${ChatColor.GRAY}[$textColor${ChatColor.BOLD}$enchant${ChatColor.GRAY}] $message")
        }
    }

    fun lorified(): String {
        return toChatColor(iconColor).toString() + ChatColor.BOLD + VERTICAL_BAR + " " + ChatColor.GRAY + enchant
    }

    fun canEnchant(item: ItemStack): Boolean {
        if (item.itemMeta.hasLore()) {
            for (lore in item.itemMeta.lore) {
                if (lore.contains(lorified())) {
                    return false
                }
            }
        }
        return canMaterialBeEnchanted(item.type)
    }

    fun canMaterialBeEnchanted(material: Material?): Boolean {
        return material == Material.DIAMOND_PICKAXE ||
                material == Material.IRON_PICKAXE ||
                material == Material.GOLD_PICKAXE ||
                material == Material.STONE_PICKAXE ||
                material == Material.WOOD_PICKAXE
    }

    fun enchantBook(level: Int, add: Boolean): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK, 1)
        val im = item.itemMeta
        im.displayName = ChatColor.YELLOW.toString() + "Enchanted Book"
        val lore: MutableList<String> = ArrayList()
        lore.add(lorified() + " " + (if (add) "+" else "") + level.toString())
        lore.add(ChatColor.GRAY.toString() + "Drag and drop onto a pickaxe")
        lore.add(ChatColor.GRAY.toString() + "to apply this enchanted book.")
        im.lore = lore
        item.itemMeta = im
        return item
    }

    fun getItemLevel(item: ItemStack): Int {
        if (item.type != Material.ENCHANTED_BOOK) {
            return -1
        }

        if (!item.itemMeta.hasDisplayName() || item.itemMeta.displayName != ChatColor.YELLOW.toString() + "Enchanted Book") {
            return -1
        }

        if (!item.itemMeta.hasLore() || item.itemMeta.lore.size <= 0) {
            return -1
        }

        if (!item.itemMeta.lore[0].contains(lorified())) {
            return -1
        }

        val lore = item.itemMeta.lore[0].split(" ").toTypedArray()

        return try {
            Integer.valueOf(lore[lore.size - 1].replace("+", ""))
        } catch (e: Exception) {
            -1
        }
    }

    fun isEnchantItem(item: ItemStack): Boolean {
        return getItemLevel(item) != -1
    }

    fun isAddEnchantItem(item: ItemStack): Boolean {
        if (!isEnchantItem(item)) {
            return false
        }
        val split = item.itemMeta.lore[0].split(" ").toTypedArray()
        return split[split.size - 1].startsWith("+")
    }

    open fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {

    }

    open fun onHold(player: Player, item: ItemStack?, level: Int) {

    }

    open fun onUnhold(player: Player) {

    }

    open fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {

    }

    open fun onSellAll(player: Player, enchantedItem: ItemStack?, level: Int, event: PlayerSellToShopEvent) {

    }

    open fun getSalvageReturns(level: Int): Long {
        var salvageFor = 0.0
        for (i in level downTo 1) {
            salvageFor += getCost(i) / 4L
        }
        return salvageFor.toLong()
    }

    abstract fun getCost(level: Int): Long

    abstract val menuDisplay: Material?

    companion object {
        const val VERTICAL_BAR = '❙'
    }

}
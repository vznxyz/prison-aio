/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.tool.ToolsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class Enchant(
    val id: String,
    val enchant: String,
    val maxLevel: Int
) {

    abstract fun getCategory(): EnchantCategory

    fun getColoredName(): String {
        return getCategory().textColor.toString() + ChatColor.BOLD.toString() + enchant
    }

    fun getStrippedName(): String {
        return ChatColor.stripColor(enchant)
    }

    fun readDescription(): String {
        return ToolsModule.config.getString("$id.description")
    }

    fun readCost(): Long {
        return ToolsModule.config.getLong("$id.cost")
    }

    fun readChance(): Double {
        return ToolsModule.config.getDouble("$id.chance")
    }

    fun readCooldown(): Long {
        return ToolsModule.config.getLong("$id.cooldown")
    }

    fun isCooldownBasedOnLevel(): Boolean {
        return ToolsModule.config.contains("$id.level-to-cooldown")
    }

    fun readLevelToCooldownMap(): Map<Int, Long> {
        val section = ToolsModule.config.getConfigurationSection("$id.level-to-cooldown")
        return section.getKeys(false).shuffled().map { it.toInt() to section.getLong(it) }.toMap()
    }

    fun sendMessage(player: Player, message: String) {
        if (!UserHandler.getUser(player).settings.isEnchantMessagesDisabled(this)) {
            player.sendMessage("${ChatColor.GRAY}[${getCategory().textColor}${ChatColor.BOLD}$enchant${ChatColor.GRAY}] $message")
        }
    }

    fun lorified(): String {
        return "${getCategory().textColor}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}$enchant"
    }

    fun canMaterialBeEnchanted(material: Material): Boolean {
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

    open fun getRefundTokens(level: Int): Long {
        var refundTokens = 0.0
        for (i in level downTo 1) {
            refundTokens += getCost(i) / 4L
        }
        return refundTokens.toLong()
    }

    fun getCost(level: Int): Long {
        return EnchantHandler.config.getEnchantPriceFormula(this).getCost(level)
    }

    abstract val menuDisplay: Material?

}
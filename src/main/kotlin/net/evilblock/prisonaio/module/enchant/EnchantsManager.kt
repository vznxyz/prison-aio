/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.type.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object EnchantsManager : Listener {

    val CHAT_PREFIX: String = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Enchants${ChatColor.GRAY}] "

    private val enchants: MutableList<AbstractEnchant> = arrayListOf()

    init {
        // old enchants
        registerEnchant(Cubed)
        registerEnchant(Explosive)
        registerEnchant(Haste)
        registerEnchant(Jump)
        registerEnchant(Speed)
        registerEnchant(Efficiency)
        registerEnchant(Fortune)
        registerEnchant(Unbreaking)
        registerEnchant(MineBomb)
        registerEnchant(Nuke)
        registerEnchant(Luck)

        // new enchants
        registerEnchant(Tokenator)
        registerEnchant(Locksmith)
        registerEnchant(TokenPouch)
        registerEnchant(Greed)
        registerEnchant(Scavenger)
        registerEnchant(JackHammer)
        registerEnchant(Exporter)
        registerEnchant(LuckyMoney)
        registerEnchant(Laser)
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val map = handleItemSwitch(event.player, event.player.itemInHand, event)
        if (map.isEmpty()) {
            return
        }

        val region = RegionsModule.findRegion(event.block.location)
        if (!region.supportsAbilityEnchants()) {
            return
        }

        for ((key, enchantLevel) in map) {
            key.onBreak(event, event.player.itemInHand, enchantLevel, region)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.item == null) {
            return
        }

        val map = handleItemSwitch(event.player, event.item, event)
        if (map.isEmpty()) {
            return
        }

        for ((key, value) in map) {
            key.onInteract(event, event.item, value)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        if (event.player.inventory.itemInMainHand != null) {
            val region = RegionsModule.findRegion(event.player.location)
            if (!region.supportsAbilityEnchants()) {
                return
            }

            val map = handleItemSwitch(event.player, event.player.inventory.itemInMainHand, event)

            for ((enchant, level) in map) {
                enchant.onSellAll(event.player, event.player.inventory.itemInMainHand, level, event)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerItemHeldEvent(event: PlayerItemHeldEvent) {
        handleItemSwitch(event.player, event.player.inventory.getItem(event.newSlot), event)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) {
            return
        }

        update(event.whoClicked as Player, event.currentItem)

        if (event.clickedInventory.type == InventoryType.PLAYER && event.whoClicked.inventory.heldItemSlot == event.slot) {
            handleItemSwitch(event.whoClicked as Player, event.cursor, event)
        }

        if (event.cursor == null || event.cursor.type != Material.ENCHANTED_BOOK) {
            return
        }

        if (event.currentItem == null) {
            return
        }

        if (event.currentItem.amount > 1) {
            return
        }

        if (!isEnchantItem(event.cursor)) {
            return
        }

        val enchant = getEnchantFromItem(event.cursor)
        if (!enchant!!.canMaterialBeEnchanted(event.currentItem.type)) {
            return
        }

        val bookLevel = enchant.getItemLevel(event.cursor)
        val itemLevel: Int = readEnchantsFromLore(event.currentItem).getOrDefault(enchant, 0)
        val level = bookLevel + itemLevel
        val vanilla = enchant is VanillaOverride

        if (enchant.isAddEnchantItem(event.cursor)) {
            if (level > enchant.maxLevel && !vanilla) {
                return
            }

            if (upgradeEnchant(event.currentItem, enchant, bookLevel, vanilla)) {
                event.cursor = null
                event.isCancelled = true
            }
        } else {
            if (itemLevel >= bookLevel || bookLevel > enchant.maxLevel && !vanilla) {
                return
            }

            if (upgradeEnchant(event.currentItem, enchant, bookLevel - itemLevel, vanilla)) {
                event.cursor = null
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerItemBreakEvent(event: PlayerItemBreakEvent) {
        handleItemSwitch(event.player, event.player.inventory.itemInMainHand, event)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        handleItemSwitch(event.player, event.player.inventory.itemInMainHand, event)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerAttemptPickupItemEvent(event: PlayerAttemptPickupItemEvent) {
        if (event.player.inventory.firstEmpty() == event.player.inventory.heldItemSlot) {
            handleItemSwitch(event.player, event.item.itemStack, event)
        }
    }

    @JvmStatic
    fun update(player: Player, newItem: ItemStack?) {
        if (newItem != null && newItem.type.toString().endsWith("_PICKAXE")) {
            if (newItem.hasItemMeta() && !newItem.itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                val im = newItem.itemMeta
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                newItem.itemMeta = im
            }
        }
    }

    @JvmStatic
    fun handleItemSwitch(player: Player, newItem: ItemStack?, event: Event?): Map<AbstractEnchant, Int> {
        update(player, newItem)

        var pickaxeData = PickaxeHandler.getPickaxeData(newItem)
        var newItem = newItem
        if (newItem != null) {
            if (pickaxeData == null && MechanicsModule.isTool(newItem)) {
                pickaxeData = PickaxeData(UUID.randomUUID())
                pickaxeData.sync(newItem)

                PickaxeHandler.trackPickaxeData(pickaxeData)

                newItem = pickaxeData.applyIdNbt(newItem)

                for ((enchant, level) in pickaxeData.enchants) {
                    if (enchant is VanillaOverride) {
                        newItem.addUnsafeEnchantment((enchant as VanillaOverride).override, level.coerceAtMost(32000))
                    }
                }

                if (event != null) {
                    when (event) {
                        is BlockBreakEvent -> {
                            event.player.inventory.itemInMainHand = newItem
                        }
                        is PlayerItemBreakEvent -> {
                            event.player.inventory.itemInMainHand = newItem
                        }
                        is PlayerInteractEvent -> {
                            event.player.inventory.itemInMainHand = newItem
                        }
                        is PlayerDropItemEvent -> {
                            event.itemDrop.itemStack = newItem
                        }
                        is PlayerAttemptPickupItemEvent -> {
                            event.item.itemStack = newItem
                        }
                        is PlayerItemHeldEvent -> {
                            event.player.inventory.setItem(event.newSlot, newItem)
                        }
                        is InventoryClickEvent -> {
                            event.cursor = newItem
                        }
                        is PlayerSellToShopEvent -> {
                            event.player.inventory.itemInMainHand = newItem
                        }
                    }

                    player.updateInventory()
                }
            }

            if (pickaxeData != null) {
                val cubedLevel = pickaxeData.enchants.getOrDefault(Cubed, 0)
                if (cubedLevel > 3) {
                    removeEnchant(newItem, Cubed)
                    addEnchant(newItem, Cubed, 3, false)

                    pickaxeData.setLevel(Cubed, 3)

                    PrisonAIO.instance.systemLog("Removed Cubed $cubedLevel from ${player.name}'s pickaxe")
                }
            }
        }

        val region = RegionsModule.findRegion(player.location)

        val map = pickaxeData?.enchants ?: emptyMap<AbstractEnchant, Int>()
        if (Bukkit.isPrimaryThread()) {
            for (enchant in enchants) {
                if (player.hasMetadata("JE-" + enchant.id)) {
                    if (map.containsKey(enchant)) {
                        if (map[enchant] != player.getMetadata("JE-" + enchant.id)[0].asInt()) {
                            enchant.onUnhold(player)
                            player.removeMetadata("JE-" + enchant.id, PrisonAIO.instance)

                            if (region.supportsPassiveEnchants()) {
                                enchant.onHold(player, newItem, map[enchant]!!)
                                player.setMetadata("JE-" + enchant.id, FixedMetadataValue(PrisonAIO.instance, map[enchant]))
                            }
                        }
                    } else {
                        enchant.onUnhold(player)
                        player.removeMetadata("JE-" + enchant.id, PrisonAIO.instance)
                    }
                } else if (map.containsKey(enchant) && region.supportsPassiveEnchants()) {
                    enchant.onHold(player, newItem, map[enchant]!!)
                    player.setMetadata("JE-" + enchant.id, FixedMetadataValue(PrisonAIO.instance, map[enchant]))
                }
            }
        }

        return map
    }

    @JvmStatic
    fun upgradeEnchant(item: ItemStack, enchant: AbstractEnchant, levels: Int, force: Boolean): Boolean {
        if (!enchant.canMaterialBeEnchanted(item.type)) {
            return false
        }

        val enchants = readEnchantsFromLore(item)
        if (!enchants.containsKey(enchant)) {
            return addEnchant(item, enchant, levels, force)
        }

        var level = enchants[enchant]!! + levels
        if (!force && level > enchant.maxLevel) {
            level = enchant.maxLevel
        }

        if (level > 32000) {
            level = 32000
        }

        if (enchant is VanillaOverride) {
            item.addUnsafeEnchantment((enchant as VanillaOverride).override, level)
        }

        val lore = item.itemMeta.lore

        lore.replaceAll { line: String ->
            if (line.contains(enchant.lorified())) {
                return@replaceAll "${enchant.lorified()} $level"
            }
            line
        }

        val im = item.itemMeta
        im.lore = lore
        item.itemMeta = im

        PickaxeHandler.getPickaxeData(item)?.setLevel(enchant, level)

        return true
    }

    @JvmStatic
    fun addEnchant(item: ItemStack, enchant: AbstractEnchant?, level: Int, force: Boolean): Boolean {
        var level = level
        if (!enchant!!.canEnchant(item)) {
            return false
        }

        if (!force && level > enchant.maxLevel) {
            level = enchant.maxLevel
        }

        if (level > 32000) {
            level = 32000
        }

        var lore = item.itemMeta.lore
        if (lore == null) {
            lore = ArrayList()
        }

        if (enchant is VanillaOverride) {
            item.addUnsafeEnchantment((enchant as VanillaOverride).override, level)
        }

        var insertAt = -1
        if (lore.isNotEmpty()) {
            for (i in lore.indices) {
                val lineAt = lore[i]
                if (ChatColor.stripColor(lineAt).startsWith(Constants.THICK_VERTICAL_LINE)) {
                    val splitLore = lineAt.split(" ").toTypedArray()
                    if (splitLore.size > 1) {
                        val intLevel = splitLore[splitLore.size - 1]
                        if (isInt(intLevel)) {
                            val loreEnchant = matchEnchant(splitLore[splitLore.size - 2])
                            if (loreEnchant != null && loreEnchant.iconColor === enchant.iconColor) {
                                insertAt = i
                            }
                        }
                    }
                }
            }
        }

        if (insertAt == -1) {
            lore.add(enchant.lorified() + " " + level)
        } else {
            lore.add(insertAt, enchant.lorified() + " " + level)
        }

        val im = item.itemMeta
        im.lore = lore
        item.itemMeta = im

        PickaxeHandler.getPickaxeData(item)?.setLevel(enchant, level)

        return true
    }

    @JvmStatic
    fun removeEnchant(item: ItemStack, enchant: AbstractEnchant): Boolean {
        if (!item.itemMeta.hasLore()) {
            return false
        }

        var found = false
        val lore = item.itemMeta.lore

        val loreIterator = lore.iterator()
        while (loreIterator.hasNext()) {
            val line = loreIterator.next()
            if (line.contains(enchant.lorified())) {
                loreIterator.remove()
                found = true
            }
        }

        if (!found) {
            return false
        }

        val im = item.itemMeta
        im.lore = lore
        item.itemMeta = im

        PickaxeHandler.getPickaxeData(item)?.removeEnchant(enchant)

        return true
    }

    @JvmStatic
    fun readEnchantsFromLore(item: ItemStack?): MutableMap<AbstractEnchant, Int> {
        val map: MutableMap<AbstractEnchant, Int> = LinkedHashMap()
        if (item == null || !item.hasItemMeta() || !item.itemMeta.hasLore()) {
            return map
        }

        for (enchant in enchants) {
            if (enchant.canMaterialBeEnchanted(item.type)) {
                for (lore in item.itemMeta.lore) {
                    if (lore.contains(enchant.lorified())) {
                        val splitLore = lore.split(" ").toTypedArray()
                        if (splitLore.size > 1) {
                            val intLevel = splitLore[splitLore.size - 1]
                            if (isInt(intLevel)) {
                                map[enchant] = Integer.valueOf(intLevel)
                                break
                            }
                        }
                    }
                }
            }
        }
        return map
    }

    @JvmStatic
    fun isEnchantItem(item: ItemStack?): Boolean {
        return getEnchantFromItem(item) != null
    }

    @JvmStatic
    fun getEnchantFromItem(item: ItemStack?): AbstractEnchant? {
        if (item == null) {
            return null
        }
        for (enchant in enchants) {
            if (enchant.isEnchantItem(item)) {
                return enchant
            }
        }
        return null
    }

    @JvmStatic
    fun matchEnchant(string: String?): AbstractEnchant? {
        for (enchant in enchants) {
            if (ChatColor.stripColor(string).equals(enchant.getStrippedEnchant(), ignoreCase = true)) {
                return enchant
            }
        }
        return null
    }

    @JvmStatic
    fun registerEnchant(enchant: AbstractEnchant) {
        enchants.add(enchant)
    }

    @JvmStatic
    fun getRegisteredEnchants(): List<AbstractEnchant> {
        return enchants.toList()
    }

    @JvmStatic
    fun getEnchantById(id: String): AbstractEnchant? {
        return enchants.first { it.id.equals(id, ignoreCase = true) }
    }

    @JvmStatic
    private fun isInt(numString: String): Boolean {
        return try {
            numString.toInt()
            true
        } catch (e: Exception) {
            false
        }
    }

}
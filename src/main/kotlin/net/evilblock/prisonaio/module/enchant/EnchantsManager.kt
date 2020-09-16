/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.type.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
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
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.util.*

object EnchantsManager : Listener {

    val CHAT_PREFIX: String = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Enchants${ChatColor.GRAY}] "

    private val dataFile: File = File(File(PrisonAIO.instance.dataFolder, "internal"),"enchants-config.json")
    private val dataType: Type = object : TypeToken<EnchantsConfig>() {}.type

    private val registeredEnchants: MutableList<AbstractEnchant> = arrayListOf()
    lateinit var config: EnchantsConfig

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

    fun loadConfig() {
        if (dataFile.exists()) {
            try {
                Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                    config = net.evilblock.cubed.Cubed.gson.fromJson(reader, dataType) as EnchantsConfig
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            config = EnchantsConfig()
        }
    }

    fun saveConfig() {
        try {
            Files.write(net.evilblock.cubed.Cubed.gson.toJson(config, dataType), dataFile, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            PrisonAIO.instance.logger.severe(ChatColor.RED.toString() + "Failed to save enchants-config.json!")
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val map = handleItemSwitch(event.player, event.player.itemInHand, event)
        if (map.isEmpty()) {
            return
        }

        val region = RegionHandler.findRegion(event.block.location)
        if (!region.supportsAbilityEnchants()) {
            return
        }

        for ((key, enchantLevel) in map) {
            if (config.isEnchantEnabled(key)) {
                key.onBreak(event, event.player.itemInHand, enchantLevel, region)
            }
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
            if (config.isEnchantEnabled(key)) {
                key.onInteract(event, event.item, value)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        if (event.player.inventory.itemInMainHand != null) {
            val region = RegionHandler.findRegion(event.player.location)
            if (!region.supportsAbilityEnchants()) {
                return
            }

            val map = handleItemSwitch(event.player, event.player.inventory.itemInMainHand, event)

            for ((enchant, level) in map) {
                if (config.isEnchantEnabled(enchant)) {
                    enchant.onSellAll(event.player, event.player.inventory.itemInMainHand, level, event)
                }
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

        update(event.currentItem)

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

        val pickaxeData = PickaxeHandler.getPickaxeData(event.currentItem) ?: return
        val itemLevel: Int = pickaxeData.enchants.getOrDefault(enchant, 0)
        val bookLevel = enchant.getItemLevel(event.cursor)
        val level = bookLevel + itemLevel
        val vanilla = enchant is VanillaOverride

        if (enchant.isAddEnchantItem(event.cursor)) {
            if (level > enchant.maxLevel && !vanilla) {
                return
            }

            val enchantLimit = pickaxeData.getEnchantLimit(enchant)
            if (level > enchantLimit) {
                event.whoClicked.sendMessage("$CHAT_PREFIX${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}levels.")
                return
            }

            if (upgradeEnchant(event.whoClicked as Player, pickaxeData, event.currentItem, enchant, bookLevel, vanilla)) {
                event.cursor = null
                event.isCancelled = true
            }
        } else {
            if (itemLevel >= bookLevel || bookLevel > enchant.maxLevel && !vanilla) {
                return
            }

            if (upgradeEnchant(event.whoClicked as Player, pickaxeData, event.currentItem, enchant, bookLevel - itemLevel, vanilla)) {
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
    fun update(newItem: ItemStack?) {
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
        update(newItem)

        var pickaxeData = PickaxeHandler.getPickaxeData(newItem)
        var newItem = newItem
        if (newItem != null) {
            if (pickaxeData == null && MechanicsModule.isPickaxe(newItem)) {
                pickaxeData = PickaxeData()
                PickaxeHandler.trackPickaxeData(pickaxeData)

                pickaxeData.sync(newItem)
                pickaxeData.applyMeta(newItem)

                newItem = pickaxeData.applyNBT(newItem)

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
                    removeEnchant(pickaxeData, newItem, Cubed)
                    addEnchant(player, pickaxeData, newItem, Cubed, 3, false)

                    player.updateInventory()

                    PrisonAIO.instance.systemLog("Removed Cubed $cubedLevel from ${player.name}'s pickaxe")
                }
            }
        }

        val region = RegionHandler.findRegion(player.location)

        val map = pickaxeData?.enchants ?: emptyMap<AbstractEnchant, Int>()
        if (Bukkit.isPrimaryThread()) {
            for (enchant in registeredEnchants) {
                if (!config.isEnchantEnabled(enchant)) {
                    continue
                }

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
    fun upgradeEnchant(player: Player, pickaxeData: PickaxeData, item: ItemStack, enchant: AbstractEnchant, levels: Int, force: Boolean): Boolean {
        if (!enchant.canMaterialBeEnchanted(item.type)) {
            return false
        }

        if (!pickaxeData.enchants.containsKey(enchant)) {
            return addEnchant(player, pickaxeData, item, enchant, levels, force)
        }

        var level = pickaxeData.enchants[enchant]!! + levels
        if (!force && level > enchant.maxLevel) {
            level = enchant.maxLevel
        }

        val enchantLimit = pickaxeData.getEnchantLimit(enchant)
        if (enchantLimit != -1 && level > enchantLimit) {
            player.sendMessage("$CHAT_PREFIX${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}levels.")
            return false
        }

        if (level > 32000) {
            level = 32000
        }

        if (enchant is VanillaOverride) {
            item.addUnsafeEnchantment((enchant as VanillaOverride).override, level)
        }

        pickaxeData.setLevel(enchant, level)
        pickaxeData.applyMeta(item)

        player.updateInventory()

        return true
    }

    @JvmStatic
    fun addEnchant(player: Player, pickaxeData: PickaxeData, item: ItemStack, enchant: AbstractEnchant?, level: Int, force: Boolean): Boolean {
        var level = level
        if (!enchant!!.canMaterialBeEnchanted(item.type)) {
            return false
        }

        if (!force && level > enchant.maxLevel) {
            level = enchant.maxLevel
        }

        val enchantLimit = pickaxeData.getEnchantLimit(enchant)
        if (enchantLimit != -1 && level > enchantLimit) {
            player.sendMessage("$CHAT_PREFIX${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}levels.")
            return false
        }

        if (level > 32000) {
            level = 32000
        }

        if (enchant is VanillaOverride) {
            item.addUnsafeEnchantment((enchant as VanillaOverride).override, level)
        }

        pickaxeData.setLevel(enchant, level)
        pickaxeData.applyMeta(item)

        return true
    }

    @JvmStatic
    fun removeEnchant(pickaxeData: PickaxeData, item: ItemStack, enchant: AbstractEnchant): Boolean {
        pickaxeData.removeEnchant(enchant)
        pickaxeData.applyMeta(item)

        if (enchant is VanillaOverride) {
            item.removeEnchantment((enchant as VanillaOverride).override)
        }

        return true
    }

    @JvmStatic
    fun readEnchantsFromLore(item: ItemStack?): MutableMap<AbstractEnchant, Int> {
        val map: MutableMap<AbstractEnchant, Int> = LinkedHashMap()
        if (item == null || !item.hasItemMeta() || !item.itemMeta.hasLore()) {
            return map
        }

        for (loreLine in item.itemMeta.lore) {
            val splitLoreLine = ChatColor.stripColor(loreLine).split(" ")
            if (splitLoreLine.size <= 1) {
                continue
            }

            val enchantName = if (splitLoreLine.size >= 3) {
                val builder = StringBuilder()

                for (i in 1 until splitLoreLine.size - 1) {
                    builder.append(splitLoreLine[i])
                }

                builder.toString()
            } else {
                splitLoreLine[1]
            }

            val enchant = getEnchantByName(enchantName)
            if (enchant != null) {
                if (!enchant.canMaterialBeEnchanted(item.type)) {
                    continue
                }

                val intLevel = splitLoreLine[splitLoreLine.size - 1]
                if (NumberUtils.isInt(intLevel)) {
                    map[enchant] = Integer.valueOf(intLevel)
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
        for (enchant in registeredEnchants) {
            if (enchant.isEnchantItem(item)) {
                return enchant
            }
        }
        return null
    }

    @JvmStatic
    fun matchEnchant(string: String?): AbstractEnchant? {
        for (enchant in registeredEnchants) {
            if (ChatColor.stripColor(string).equals(enchant.getStrippedEnchant(), ignoreCase = true)) {
                return enchant
            }
        }
        return null
    }

    @JvmStatic
    fun registerEnchant(enchant: AbstractEnchant) {
        registeredEnchants.add(enchant)
    }

    @JvmStatic
    fun getRegisteredEnchants(): List<AbstractEnchant> {
        return registeredEnchants.toList()
    }

    @JvmStatic
    fun getEnchantById(id: String): AbstractEnchant? {
        return registeredEnchants.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    @JvmStatic
    fun getEnchantByName(name: String): AbstractEnchant? {
        return registeredEnchants.firstOrNull { it.enchant.equals(name, ignoreCase = true) }
    }

    private val enchantColorOrder = mapOf<Color, Int>(
        Color.RED to 1,
        Color.ORANGE to 2,
        Color.YELLOW to 3,
        Color.LIME to 4,
        Color.AQUA to 5
    )

    val ENCHANT_COMPARATOR = Comparator<AbstractEnchant> { o1, o2 ->
        val o1Order = enchantColorOrder.getOrDefault(o1.iconColor, 4)
        val o2Order = enchantColorOrder.getOrDefault(o2.iconColor, 4)
        when {
            o1Order == o2Order -> {
                return@Comparator 0
            }
            o1Order > o2Order -> {
                return@Comparator 1
            }
            else -> {
                return@Comparator -1
            }
        }
    }

    val MAPPED_ENCHANT_COMPARATOR = Comparator<Map.Entry<AbstractEnchant, Int>> { o1, o2 ->
        val o1Order = enchantColorOrder.getOrDefault(o1.key.iconColor, 4)
        val o2Order = enchantColorOrder.getOrDefault(o2.key.iconColor, 4)
        when {
            o1Order == o2Order -> {
                return@Comparator 0
            }
            o1Order > o2Order -> {
                return@Comparator 1
            }
            else -> {
                return@Comparator -1
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.tool.enchant.impl.*
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.tool.enchant.config.EnchantsConfig
import net.evilblock.prisonaio.module.tool.enchant.config.formula.FixedPriceFormulaType
import net.evilblock.prisonaio.module.tool.enchant.config.formula.BasePriceWithFixedModifierFormulaType
import net.evilblock.prisonaio.module.tool.enchant.config.formula.FixedRateFormulaType
import net.evilblock.prisonaio.module.tool.enchant.config.formula.PriceFormulaType
import net.evilblock.prisonaio.module.tool.enchant.override.VanillaOverride
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.util.*

object EnchantHandler : Listener {

    val CHAT_PREFIX: String = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Enchants${ChatColor.GRAY}] "

    private val dataFile: File = File(File(PrisonAIO.instance.dataFolder, "internal"),"enchants-config.json")
    private val dataType: Type = object : TypeToken<EnchantsConfig>() {}.type

    lateinit var config: EnchantsConfig

    private val registeredEnchants: MutableList<Enchant> = arrayListOf()

    private val registeredPriceFormulaTypes: MutableList<PriceFormulaType> = arrayListOf(
        BasePriceWithFixedModifierFormulaType,
        FixedPriceFormulaType,
        FixedRateFormulaType
    )

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
        registerEnchant(Zeus)
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
        val pickaxe = PickaxeHandler.getPickaxeData(event.player.inventory.itemInMainHand)
        if (pickaxe != null) {
            val region = RegionHandler.findRegion(event.block.location)
            if (!region.supportsAbilityEnchants()) {
                return
            }

            for ((enchant, level) in pickaxe.enchants) {
                if (config.isEnchantEnabled(enchant) && !pickaxe.isEnchantDisabled(enchant)) {
                    enchant.onBreak(event, event.player.itemInHand, level, region)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.item == null) {
            return
        }

        val pickaxe = PickaxeHandler.getPickaxeData(event.player.inventory.itemInMainHand)
        if (pickaxe != null) {
            for ((enchant, level) in pickaxe.enchants) {
                if (config.isEnchantEnabled(enchant) && !pickaxe.isEnchantDisabled(enchant)) {
                    enchant.onInteract(event, event.item, level)

                    if (event.isCancelled) {
                        break
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        val itemInHand = event.player.inventory.itemInMainHand

        val pickaxe = PickaxeHandler.getPickaxeData(itemInHand)
        if (pickaxe != null) {
            val region = RegionHandler.findRegion(event.player.location)
            if (!region.supportsAbilityEnchants()) {
                return
            }

            for ((enchant, level) in pickaxe.enchants) {
                if (config.isEnchantEnabled(enchant) && !pickaxe.isEnchantDisabled(enchant)) {
                    enchant.onSellAll(event.player, itemInHand, level, event)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerItemHeldEvent(event: PlayerItemHeldEvent) {
        val newItem = event.player.inventory.getItem(event.newSlot)

        var pickaxe = PickaxeHandler.getPickaxeData(newItem)
        if (pickaxe == null) {
            if (MechanicsModule.isPickaxe(newItem)) {
                println("New pickaxe registered for ${event.player.name}")
                pickaxe = PickaxeData().also {
                    it.sync(newItem)
                    PickaxeHandler.trackPickaxeData(it)

                    event.player.inventory.setItem(event.newSlot, it.toItemStack(newItem))
                    event.player.updateInventory()
                }
            }
        }

        val region = RegionHandler.findRegion(event.player.location)

        if (Bukkit.isPrimaryThread()) {
            for (enchant in registeredEnchants) {
                if (!config.isEnchantEnabled(enchant)) {
                    continue
                }

                if (event.player.hasMetadata("JE-" + enchant.id)) {
                    if (pickaxe?.enchants?.containsKey(enchant) == true) {
                        if (pickaxe.enchants[enchant] != event.player.getMetadata("JE-" + enchant.id)[0].asInt()) {
                            enchant.onUnhold(event.player)
                            event.player.removeMetadata("JE-" + enchant.id, PrisonAIO.instance)

                            if (pickaxe.isEnchantDisabled(enchant)) {
                                continue
                            }

                            if (region.supportsPassiveEnchants()) {
                                enchant.onHold(event.player, newItem, pickaxe.enchants[enchant]!!)
                                event.player.setMetadata("JE-" + enchant.id, FixedMetadataValue(PrisonAIO.instance, pickaxe.enchants[enchant]))
                            }
                        }
                    } else {
                        enchant.onUnhold(event.player)
                        event.player.removeMetadata("JE-" + enchant.id, PrisonAIO.instance)
                    }
                } else if (pickaxe?.enchants?.containsKey(enchant) == true && region.supportsPassiveEnchants()) {
                    if (pickaxe.isEnchantDisabled(enchant)) {
                        continue
                    }

                    enchant.onHold(event.player, newItem, pickaxe.enchants[enchant]!!)
                    event.player.setMetadata("JE-" + enchant.id, FixedMetadataValue(PrisonAIO.instance, pickaxe.enchants[enchant]))
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) {
            return
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

        var pickaxe = PickaxeHandler.getPickaxeData(event.currentItem)
        if (pickaxe == null) {
            if (MechanicsModule.isPickaxe(event.currentItem)) {
                println("New pickaxe registered for ${event.whoClicked.name}")
                pickaxe = PickaxeData().also {
                    it.sync(event.currentItem)
                    PickaxeHandler.trackPickaxeData(it)

                    event.currentItem = it.toItemStack(event.currentItem)
                    (event.whoClicked as Player).updateInventory()
                }
            }
        }

        if (pickaxe == null) {
            return
        }

        val itemLevel: Int = pickaxe.enchants.getOrDefault(enchant, 0)
        val bookLevel = enchant.getItemLevel(event.cursor)
        val newLevel = bookLevel + itemLevel
        val force = enchant is VanillaOverride

        if (enchant.isAddEnchantItem(event.cursor)) {
            if (newLevel > enchant.maxLevel && !force) {
                return
            }

            val enchantLimit = pickaxe.getEnchantLimit(enchant)
            if (enchantLimit != -1 && newLevel > enchantLimit) {
                event.whoClicked.sendMessage("$CHAT_PREFIX${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedName()} ${ChatColor.RED}levels.")
                return
            }

            if (upgradeEnchant(event.whoClicked as Player, pickaxe, event.currentItem, enchant, bookLevel, force)) {
                event.cursor = null
                event.isCancelled = true
            }
        } else {
            if (itemLevel >= bookLevel || bookLevel > enchant.maxLevel && !force) {
                return
            }

            if (upgradeEnchant(event.whoClicked as Player, pickaxe, event.currentItem, enchant, bookLevel - itemLevel, force)) {
                event.cursor = null
                event.isCancelled = true
            }
        }
    }

    @JvmStatic
    fun upgradeEnchant(player: Player, pickaxeData: PickaxeData, item: ItemStack, enchant: Enchant, levels: Int, force: Boolean): Boolean {
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
            player.sendMessage("$CHAT_PREFIX${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedName()} ${ChatColor.RED}levels.")
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
    fun addEnchant(player: Player, pickaxeData: PickaxeData, item: ItemStack, enchant: Enchant?, level: Int, force: Boolean): Boolean {
        var level = level
        if (!enchant!!.canMaterialBeEnchanted(item.type)) {
            return false
        }

        if (!force && level > enchant.maxLevel) {
            level = enchant.maxLevel
        }

        val enchantLimit = pickaxeData.getEnchantLimit(enchant)
        if (enchantLimit != -1 && level > enchantLimit) {
            player.sendMessage("$CHAT_PREFIX${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedName()} ${ChatColor.RED}levels.")
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
    fun removeEnchant(pickaxeData: PickaxeData, item: ItemStack, enchant: Enchant): Boolean {
        pickaxeData.removeEnchant(enchant)
        pickaxeData.applyMeta(item)

        if (enchant is VanillaOverride) {
            item.removeEnchantment((enchant as VanillaOverride).override)
        }

        return true
    }

    @JvmStatic
    fun readEnchantsFromLore(item: ItemStack?): MutableMap<Enchant, Int> {
        val map: MutableMap<Enchant, Int> = LinkedHashMap()
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
                    builder.append(splitLoreLine[i]).append(" ")
                }

                builder.toString().trim()
            } else {
                splitLoreLine[1]
            }

            val enchant = getEnchantByName(enchantName)
            if (enchant != null) {
                if (!enchant.canMaterialBeEnchanted(item.type)) {
                    continue
                }

                val level = splitLoreLine[splitLoreLine.size - 1].replace(",", "").replace(".", "")
                if (NumberUtils.isInt(level)) {
                    map[enchant] = Integer.valueOf(level)
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
    fun getEnchantFromItem(item: ItemStack?): Enchant? {
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
    fun matchEnchant(string: String?): Enchant? {
        for (enchant in registeredEnchants) {
            if (ChatColor.stripColor(string).equals(enchant.getStrippedName(), ignoreCase = true)) {
                return enchant
            }
        }
        return null
    }

    @JvmStatic
    fun registerEnchant(enchant: Enchant) {
        registeredEnchants.add(enchant)
    }

    @JvmStatic
    fun getRegisteredEnchants(): List<Enchant> {
        return registeredEnchants.toList()
    }

    @JvmStatic
    fun getRegistedPriceFormulaTypes(): List<PriceFormulaType> {
        return registeredPriceFormulaTypes
    }

    @JvmStatic
    fun getEnchantById(id: String): Enchant? {
        return registeredEnchants.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    @JvmStatic
    fun getEnchantByName(name: String): Enchant? {
        return registeredEnchants.firstOrNull { it.enchant.equals(name, ignoreCase = true) }
    }

    val ENCHANT_COMPARATOR = Comparator<Enchant> { o1, o2 ->
        val o1Order = o1.getCategory().ordinal
        val o2Order = o2.getCategory().ordinal
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

    val MAPPED_ENCHANT_COMPARATOR = Comparator<Map.Entry<Enchant, Int>> { o1, o2 ->
        val o1Order = o1.key.getCategory().ordinal
        val o2Order = o2.key.getCategory().ordinal
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
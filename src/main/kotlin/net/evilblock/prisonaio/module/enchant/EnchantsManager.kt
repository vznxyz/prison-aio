package net.evilblock.prisonaio.module.enchant

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.menu.PurchaseEnchantMenu
import net.evilblock.prisonaio.module.enchant.type.*
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.mechanic.region.Regions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object EnchantsManager : Listener {

    val CHAT_PREFIX: String = ChatColor.GRAY.toString() + "[" + ChatColor.RED + ChatColor.BOLD + "Enchants" + ChatColor.GRAY + "] "

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

    @EventHandler
    fun onPlayerInteractEventSign(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (event.clickedBlock.type != Material.WALL_SIGN) {
            return
        }

        val sign = event.clickedBlock.state as Sign
        if (sign.getLine(0) != ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Enchant") {
            return
        }

        PurchaseEnchantMenu.tryOpeningMenu(event.player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val map = handleItemSwitch(event.player, event.player.itemInHand)
        if (map.isEmpty()) {
            return
        }

        val region = Regions.findRegion(event.block.location) ?: return
        if (!region.supportsEnchants()) {
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

        val map = handleItemSwitch(event.player, event.item)
        if (map.isEmpty()) {
            return
        }

        for ((key, value) in map) {
            key.onInteract(event, event.item, value)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onShopSellEvent(event: PlayerSellToShopEvent) {
        if (event.player.itemInHand != null) {
            val map = handleItemSwitch(event.player, event.player.itemInHand)

            map.forEach { (enchant, level) ->
                enchant.onSellAll(event.player, event.player.itemInHand, level, event)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHold(event: PlayerItemHeldEvent) {
        handleItemSwitch(event.player, event.player.inventory.getItem(event.newSlot))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) {
            return
        }

        update(event.currentItem)

        if (event.clickedInventory.type == InventoryType.PLAYER && event.whoClicked.inventory.heldItemSlot == event.slot) {
            handleItemSwitch(event.whoClicked as Player, event.cursor)
        }

        if (event.cursor == null || event.cursor.type != Material.ENCHANTED_BOOK) {
            return
        }

        if (event.currentItem == null) {
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
        val itemLevel: Int = getEnchants(event.currentItem).getOrDefault(enchant, 0)
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
        handleItemSwitch(event.player, event.player.inventory.itemInMainHand)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerItemDropEvent(event: PlayerDropItemEvent) {
        handleItemSwitch(event.player, event.player.inventory.itemInMainHand)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerAttemptPickupItemEvent(event: PlayerAttemptPickupItemEvent) {
        if (event.player.inventory.firstEmpty() == event.player.inventory.heldItemSlot) {
            handleItemSwitch(event.player, event.item.itemStack)
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

            val map = getEnchants(newItem)
            for (enchant in enchants) {
                if (enchant is VanillaOverride) {
                    val override = enchant as VanillaOverride
                    if (newItem.enchantments.getOrDefault(override.override, 0) != map.getOrDefault(enchant, 0)) {
                        val diff = newItem.enchantments.getOrDefault(override.override, 0) - map.getOrDefault(enchant, 0)
                        upgradeEnchant(newItem, enchant, diff, true)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun update(player: Player, newItem: ItemStack?) {
        update(newItem)
    }

    @JvmStatic
    fun handleItemSwitch(player: Player, newItem: ItemStack?): Map<AbstractEnchant, Int> {
        update(newItem)

        val map = getEnchants(newItem)

        for (enchant in enchants) {
            if (player.hasMetadata("JE-" + enchant.enchant)) {
                if (map.containsKey(enchant)) {
                    if (map[enchant] != player.getMetadata("JE-" + enchant.enchant)[0].asInt()) {
                        enchant.onUnhold(player)
                        player.removeMetadata("JE-" + enchant.enchant, PrisonAIO.instance)
                        enchant.onHold(player, newItem, map[enchant]!!)
                        player.setMetadata("JE-" + enchant.enchant, FixedMetadataValue(PrisonAIO.instance, map[enchant]))
                    }
                } else {
                    enchant.onUnhold(player)
                    player.removeMetadata("JE-" + enchant.enchant, PrisonAIO.instance)
                }
            } else if (map.containsKey(enchant)) {
                enchant.onHold(player, newItem, map[enchant]!!)
                player.setMetadata("JE-" + enchant.enchant, FixedMetadataValue(PrisonAIO.instance, map[enchant]))
            }
        }
        return map
    }

    @JvmStatic
    fun upgradeEnchant(item: ItemStack, enchant: AbstractEnchant, levels: Int, force: Boolean): Boolean {
        if (!enchant.canMaterialBeEnchanted(item.type)) {
            return false
        }

        val enchants = getEnchants(item)
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
        val levelReplacer = level
        lore.replaceAll { line: String ->
            if (line.contains(enchant.lorified())) {
                return@replaceAll enchant.lorified() + " " + levelReplacer
            }
            line
        }
        val im = item.itemMeta
        im.lore = lore
        item.itemMeta = im
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
                if (ChatColor.stripColor(lineAt).startsWith(Character.toString(AbstractEnchant.VERTICAL_BAR))) {
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
        return true
    }

    @JvmStatic
    fun getEnchants(item: ItemStack?): Map<AbstractEnchant, Int> {
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
            if (ChatColor.stripColor(string).equals(enchant.strippedEnchant, ignoreCase = true)) {
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
    private fun isInt(numString: String): Boolean {
        return try {
            numString.toInt()
            true
        } catch (e: Exception) {
            false
        }
    }

}
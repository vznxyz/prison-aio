/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.nms.NBTUtil
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.module.tool.enchant.serialize.EnchantsMapReferenceSerializer
import net.evilblock.prisonaio.module.tool.enchant.serialize.EnchantsSetReferenceSerializer
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat
import java.util.*

class PickaxeData(val uuid: UUID = UUID.randomUUID()) {

    var customName: String? = null

    var prestige: Int = 0
    var blocksMined: Int = 0

    @JsonAdapter(EnchantsMapReferenceSerializer::class)
    var enchants: MutableMap<AbstractEnchant, Int> = hashMapOf()

    @JsonAdapter(EnchantsSetReferenceSerializer::class)
    var disabledEnchants: MutableSet<AbstractEnchant> = hashSetOf()

    fun addLevels(enchant: AbstractEnchant, levels: Int) {
        if (!enchants.containsKey(enchant)) {
            enchants[enchant] = levels
        } else {
            enchants[enchant] = enchants[enchant]!! + levels
        }
    }

    fun setLevel(enchant: AbstractEnchant, level: Int) {
        enchants[enchant] = level
    }

    fun removeEnchant(enchant: AbstractEnchant) {
        enchants.remove(enchant)
    }

    fun sync(itemStack: ItemStack) {
        synchronized(modificationLock) {
            enchants = EnchantsManager.readEnchantsFromLore(itemStack)

            if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) {
                for (line in itemStack.itemMeta.lore) {
                    try {
                        if (line.contains("Prestige")) {
                            val level = line.split(" ")[2]
                            if (NumberUtils.isInt(level)) {
                                prestige = Integer.valueOf(level)
                            }
                        } else if (line.contains("Blocks Mined")) {
                            val level = line.split(" ")[3]
                            if (NumberUtils.isInt(level)) {
                                blocksMined = Integer.valueOf(level)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun applyNBT(itemStack: ItemStack): ItemStack {
        synchronized(modificationLock) {
            return ItemUtils.addUUIDToItemTag(itemStack, "PickaxeID", uuid, true)
        }
    }

    fun applyMeta(itemStack: ItemStack) {
        synchronized(modificationLock) {
            val lore = arrayListOf<String>()

            lore.add("")
            lore.add("${ChatColor.YELLOW}${ChatColor.BOLD}Statistics")
            lore.add("${ChatColor.GRAY}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}Prestige: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(prestige)}")
            lore.add("${ChatColor.GRAY}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}Blocks Mined: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberFormat.getInstance().format(blocksMined)}")
            lore.add("")
            lore.add("${ChatColor.RED}${ChatColor.BOLD}Enchants")

            if (enchants.isNotEmpty()) {
                for ((enchant, level) in enchants.entries.sortedWith(EnchantsManager.MAPPED_ENCHANT_COMPARATOR)) {
                    lore.add("${enchant.lorified()} ${NumberUtils.format(level)}")
                }
            } else {
                lore.add("${ChatColor.GRAY}No enchants applied!")
            }

            if (itemStack.itemMeta != null) {
                val meta = itemStack.itemMeta
                meta.lore = lore
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

                itemStack.itemMeta = meta
            }
        }
    }

    fun toItemStack(original: ItemStack): ItemStack {
        return applyNBT(original).also {
            applyMeta(it)
        }
    }

    /**
     * Finds the enchantment limit for this pickaxe.
     */
    fun getEnchantLimit(enchant: AbstractEnchant): Int {
        return PickaxePrestigeHandler.findEnchantLimits(prestige).getOrDefault(enchant, -1)
    }

    fun isEnchantDisabled(enchant: AbstractEnchant): Boolean {
        return disabledEnchants.contains(enchant)
    }

    fun toggleEnchant(enchant: AbstractEnchant) {
        if (isEnchantDisabled(enchant)) {
            disabledEnchants.remove(enchant)
        } else {
            disabledEnchants.add(enchant)
        }
    }

    companion object {
        private val modificationLock = Object()
    }

}
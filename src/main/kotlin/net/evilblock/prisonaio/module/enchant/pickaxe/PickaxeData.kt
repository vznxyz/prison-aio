/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.pickaxe

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.module.enchant.serialize.EnchantsMapReferenceSerializer
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

class PickaxeData(val uuid: UUID = UUID.randomUUID()) {

    var prestige: Int = 0

    @JsonAdapter(EnchantsMapReferenceSerializer::class)
    var enchants: MutableMap<AbstractEnchant, Int> = hashMapOf()

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
        if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) {
            val firstLine = itemStack.itemMeta.lore?.firstOrNull()
            if (firstLine != null && firstLine.contains("Prestige")) {
                val splitLore = firstLine.split(" ").toTypedArray()
                if (splitLore.size > 1) {
                    val intString = splitLore[splitLore.size - 1]
                    if (NumberUtils.isInt(intString)) {
                        prestige = intString.toInt()
                    }
                }
            }
        }

        enchants = EnchantsManager.readEnchantsFromLore(itemStack)
    }

    fun applyNBT(itemStack: ItemStack): ItemStack {
        val nmsItemStack = CraftItemStack.asNMSCopy(itemStack)
        var tag: NBTTagCompound? = nmsItemStack.tag

        if (tag == null) {
            tag = NBTTagCompound()
            nmsItemStack.tag = tag
        }

        tag.setUUID("PickaxeID", uuid)

        return CraftItemStack.asBukkitCopy(nmsItemStack)
    }

    fun applyLore(itemStack: ItemStack) {
        val lore = arrayListOf<String>()

        if (prestige > 0) {
            lore.add("${ChatColor.RED}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}Prestige $prestige")
        }

        for ((enchant, level) in enchants.entries.sortedWith(EnchantsManager.ENCHANT_COMPARATOR)) {
            lore.add("${enchant.lorified()} $level")
        }

        if (itemStack.itemMeta != null) {
            val meta = itemStack.itemMeta
            meta.lore = lore

            itemStack.itemMeta = meta
        }
    }

    fun getEnchantLimit(enchant: AbstractEnchant): Int {
        val pickaxePrestige = PickaxePrestigeHandler.getNextPrestige(prestige)
        if (pickaxePrestige != null && pickaxePrestige.enchantLimits.containsKey(enchant)) {
            return pickaxePrestige.enchantLimits[enchant]!!
        }
        return -1
    }

}
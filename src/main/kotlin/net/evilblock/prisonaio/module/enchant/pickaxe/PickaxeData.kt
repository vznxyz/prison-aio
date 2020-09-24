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
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat
import java.util.*

class PickaxeData(val uuid: UUID = UUID.randomUUID()) {

    var prestige: Int = 0
    var blocksMined: Int = 0

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

    fun applyMeta(itemStack: ItemStack) {
        val lore = arrayListOf<String>()

        lore.add("${ChatColor.GREEN}${ChatColor.BOLD}Statistics")
        lore.add("${ChatColor.GREEN}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}Prestige: ${NumberUtils.format(prestige)}")
        lore.add("${ChatColor.GREEN}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}Blocks Mined: ${NumberFormat.getInstance().format(blocksMined)}")
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

    /**
     * Finds the enchantment limit for this pickaxe.
     */
    fun getEnchantLimit(enchant: AbstractEnchant): Int {
        val nextPrestige = PickaxePrestigeHandler.getNextPrestige(prestige)
        if (nextPrestige != null) {
            if (nextPrestige.enchantLimits.containsKey(enchant)) {
                return nextPrestige.enchantLimits[enchant]!!
            }
        } else {
            val maxPrestige = PickaxePrestigeHandler.getMaxPrestige()
            if (maxPrestige != null) {
                if (prestige >= maxPrestige.number) {
                    for (prestige in PickaxePrestigeHandler.getPrestigeSet().filter { it.number < maxPrestige.number }.sortedBy { it.number }.reversed()) {
                        if (prestige.enchantLimits.containsKey(enchant)) {
                            return prestige.enchantLimits[enchant]!!
                        }
                    }
                }
            }
        }

        for (prestige in PickaxePrestigeHandler.getPrestigeSet().filter { it.number < prestige }.sortedBy { it.number }.reversed()) {
            if (prestige.enchantLimits.containsKey(enchant)) {
                return prestige.enchantLimits[enchant]!!
            }
        }

        return -1
    }

}
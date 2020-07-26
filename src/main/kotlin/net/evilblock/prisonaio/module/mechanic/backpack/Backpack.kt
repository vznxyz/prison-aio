/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.mechanic.backpack.enchant.BackpackEnchant
import net.evilblock.prisonaio.module.mechanic.backpack.menu.BackpackMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Backpack(val id: String) {

    internal val contents: MutableMap<Int, ItemStack> = hashMapOf()
    internal val enchants: MutableMap<BackpackEnchant, Int> = hashMapOf()

    fun addItem(itemStack: ItemStack): Boolean {
        var remainingAmount = itemStack.amount
        val insertTo = hashMapOf<Int, Int>()

        for (slot in contents) {
            if (slot.value.amount >= slot.value.maxStackSize) {
                continue
            }

            if (!ItemUtils.isSimilar(slot.value, itemStack) || !ItemUtils.hasSameLore(slot.value, itemStack) || !ItemUtils.hasSameEnchantments(slot.value, itemStack)) {
                continue
            }

            val maxInsert = slot.value.maxStackSize - slot.value.amount
            if (maxInsert <= 0) {
                continue
            }

            if (remainingAmount <= maxInsert) {
                insertTo[slot.key] = remainingAmount
                remainingAmount = 0
            } else {
                insertTo[slot.key] = maxInsert
                remainingAmount -= maxInsert
            }

            if (remainingAmount <= 0) {
                break
            }
        }

        if (remainingAmount > 0) {
            for (i in 0..getMaxSlots()) {
                if (!contents.containsKey(i)) {
                    insertTo[i] = remainingAmount
                    remainingAmount = 0
                    break
                }
            }

            if (remainingAmount > 0) {
                return false
            }
        }

        for (insert in insertTo) {
            if (contents.containsKey(insert.key)) {
                val itemAtSlot = contents[insert.key]!!
                itemAtSlot.amount = itemAtSlot.amount + insert.value
            } else {
                val clonedItem = itemStack.clone()
                clonedItem.amount = insert.value

                contents[insert.key] = clonedItem
            }
        }

        return true
    }

    fun hasEnchant(enchant: BackpackEnchant): Boolean {
        return enchants.containsKey(enchant)
    }

    fun getMaxSlots(): Int {
        return 64
    }

    fun getItemsSize(): Int {
        return contents.values.sumBy { it.amount }
    }

    fun open(player: Player) {
        BackpackMenu(this).openMenu(player)
    }

    fun toBackpackItem(): ItemStack {
        val item = ItemBuilder.of(Material.CHEST)
            .name("${ChatColor.RED}${ChatColor.BOLD}Backpack")
            .build()

        updateBackpackItemLore(item)

        return item
    }

    fun updateBackpackItemLore(itemStack: ItemStack) {
        val lore = arrayListOf<String>()
        lore.add("${ChatColor.GRAY}(ID: #$id)")
        lore.add("")
        lore.add("${ChatColor.RED}${ChatColor.BOLD}Enchants")

        if (enchants.isEmpty()) {
            lore.add("${ChatColor.GRAY}None")
        } else {
            for (enchant in enchants) {
                lore.add("${enchant.key.lorified()} ${enchant.value}")
            }
        }

        lore.add("")
        lore.add("${ChatColor.GRAY}Right-click while holding this")
        lore.add("${ChatColor.GRAY}backpack in your hand to open")
        lore.add("${ChatColor.GRAY}its items!")

        itemStack.lore = lore
    }

}
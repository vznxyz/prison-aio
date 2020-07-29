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
import java.util.*

class Backpack(val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 13)) {

    internal val contents: MutableMap<Int, ItemStack> = hashMapOf()
    internal val enchants: MutableMap<BackpackEnchant, Int> = hashMapOf()

    fun addItem(itemStack: ItemStack): ItemStack? {
        var remainingAmount = itemStack.amount

        for (slotItem in contents.values) {
            if (slotItem.amount >= slotItem.maxStackSize) {
                continue
            }

            if (!ItemUtils.isSimilar(slotItem, itemStack) || !ItemUtils.hasSameLore(slotItem, itemStack) || !ItemUtils.hasSameEnchantments(slotItem, itemStack)) {
                continue
            }

            val maxInsert = slotItem.maxStackSize - slotItem.amount
            if (maxInsert <= 0) {
                continue
            }

            if (remainingAmount <= maxInsert) {
                slotItem.amount = slotItem.amount + remainingAmount
                remainingAmount = 0
            } else {
                slotItem.amount = slotItem.amount + maxInsert
                remainingAmount -= maxInsert
            }

            if (remainingAmount <= 0) {
                break
            }
        }

        if (remainingAmount > 0) {
            for (i in 0..getMaxSlots()) {
                if (!contents.containsKey(i)) {
                    contents[i] = ItemBuilder.copyOf(itemStack).amount(remainingAmount.coerceAtMost(itemStack.type.maxStackSize)).build()
                    remainingAmount -= remainingAmount.coerceAtMost(itemStack.type.maxStackSize)
                    break
                }
            }
        }

        return if (remainingAmount > 0) {
            return ItemBuilder.copyOf(itemStack).amount(remainingAmount).build()
        } else {
            null
        }
    }

    fun removeItem(itemStack: ItemStack) {
        var remainingAmount = itemStack.amount

        val toRemove = arrayListOf<Int>()
        for ((slot, slotItem) in contents) {
            if (slotItem.amount <= 0) {
                continue
            }

            if (!ItemUtils.isSimilar(slotItem, itemStack) || !ItemUtils.hasSameLore(slotItem, itemStack) || !ItemUtils.hasSameEnchantments(slotItem, itemStack)) {
                continue
            }

            val maxTake = slotItem.amount.coerceAtMost(remainingAmount)
            if (maxTake <= 0) {
                continue
            }

            if (remainingAmount >= maxTake) {
                toRemove.add(slot)
                remainingAmount -= maxTake
            } else {
                slotItem.amount = slotItem.amount - maxTake
                remainingAmount = 0
            }

            if (remainingAmount <= 0) {
                break
            }
        }

        for (i in toRemove) {
            contents.remove(i)
        }
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
        lore.add("${ChatColor.GRAY}backpack in your hand to access")
        lore.add("${ChatColor.GRAY}its contents!")

        itemStack.lore = lore
    }

}
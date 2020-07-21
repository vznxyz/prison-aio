/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.mechanic.backpack.enchant.BackpackEnchant
import net.evilblock.prisonaio.module.mechanic.backpack.menu.BackpackMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Backpack(val id: String) {

    internal val contents: MutableMap<Int, ItemStack> = hashMapOf()
    internal val enchants: MutableMap<BackpackEnchant, Int> = hashMapOf()

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
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.impl

import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object WardenArmorSet : AbilityArmorSet(
    "Warden",
    "${ChatColor.DARK_RED}${ChatColor.BOLD}Warden",
    ItemStack(Material.DIAMOND_HELMET),
    ItemStack(Material.DIAMOND_CHESTPLATE),
    ItemStack(Material.DIAMOND_LEGGINGS),
    ItemStack(Material.DIAMOND_BOOTS)
) {

    override fun getSetDescription(): String {
        return "While the full set is equipped, you will receive 2x more tokens when mining."
    }

    override fun getShortSetDescription(): String? {
        return "Receive 2x more tokens when mining"
    }

    override fun getInheritedArmorSets(): List<AbilityArmorSet> {
        return listOf(InmateArmorSet, MinerArmorSet)
    }

}
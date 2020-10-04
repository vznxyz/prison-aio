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
        return "While the full set is equipped, right-click while mining to activate the Rage ability, which has no description yet because I don't like the original idea."
    }

    override fun getInheritedArmorSets(): List<AbilityArmorSet> {
        return listOf(InmateArmorSet, MinerArmorSet)
    }

}
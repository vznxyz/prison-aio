/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import net.evilblock.prisonaio.module.tool.enchant.override.VanillaOverride
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

object Fortune : Enchant("fortune", "Fortune", 10000), VanillaOverride {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.WEALTH_HIGH
    }

    override val override: Enchantment
        get() = Enchantment.LOOT_BONUS_BLOCKS

//    override fun getCost(level: Int): Long {
//        return (3300 + (level - 1) * 30).toLong()
//    }

    override val menuDisplay: Material
        get() = Material.DIAMOND

}
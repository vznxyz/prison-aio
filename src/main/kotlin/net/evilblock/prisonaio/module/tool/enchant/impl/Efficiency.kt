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

object Efficiency : Enchant("efficiency", "Efficiency", 1000), VanillaOverride {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.VANILLA
    }

    override val override: Enchantment
        get() = Enchantment.DIG_SPEED

//    override fun getCost(level: Int): Long {
//        return (2000 + (level - 1) * 40).toLong()
//    }

    override val menuDisplay: Material
        get() = Material.EXP_BOTTLE
}
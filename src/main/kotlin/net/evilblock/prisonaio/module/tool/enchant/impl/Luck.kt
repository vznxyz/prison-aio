/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import org.bukkit.Material

object Luck : Enchant("luck", "Luck", 4) {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.WEALTH_LOW
    }

    override val menuDisplay: Material
        get() = Material.RABBIT_FOOT

//    override fun getCost(level: Int): Long {
//        return (6000 + (level - 1) * 1200).toLong()
//    }

}
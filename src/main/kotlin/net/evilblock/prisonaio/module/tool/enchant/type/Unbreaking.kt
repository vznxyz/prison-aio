/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

object Unbreaking : AbstractEnchant("unbreaking", "Unbreaking", 1000), VanillaOverride {

    override val iconColor: Color
        get() = Color.LIME

    override val textColor: ChatColor
        get() = ChatColor.GREEN

    override val override: Enchantment
        get() = Enchantment.DURABILITY

    override val menuDisplay: Material
        get() = Material.ANVIL

//    override fun getCost(level: Int): Long {
//        return (3500 + (level - 1) * 40).toLong()
//    }

}
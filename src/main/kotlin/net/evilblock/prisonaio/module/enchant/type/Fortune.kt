/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

object Fortune : AbstractEnchant("fortune", "Fortune", 10000), VanillaOverride {

    override val iconColor: Color
        get() = Color.AQUA

    override val textColor: ChatColor
        get() = ChatColor.AQUA

    override val override: Enchantment
        get() = Enchantment.LOOT_BONUS_BLOCKS

    override fun getCost(level: Int): Long {
        return (3300 + (level - 1) * 30).toLong()
    }

    override val menuDisplay: Material
        get() = Material.DIAMOND

}
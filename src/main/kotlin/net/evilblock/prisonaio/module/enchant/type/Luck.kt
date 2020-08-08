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

object Luck : AbstractEnchant("luck", "Luck", 4) {

    override val iconColor: Color
        get() = Color.LIME

    override val textColor: ChatColor
        get() = ChatColor.GREEN

    override val menuDisplay: Material
        get() = Material.RABBIT_FOOT

    override fun getCost(level: Int): Long {
        return (6000 + (level - 1) * 1200).toLong()
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.enchant

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ColorUtil
import org.bukkit.ChatColor
import org.bukkit.Color

interface BackpackEnchant {

    fun getName(): String

    fun getColor(): Color

    fun getDescription(): String

    fun getCost(level: Int)

    fun getMaxLevel()

    fun lorified(): String {
        return "${ColorUtil.toChatColor(getColor())}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}${getName()}"
    }

}
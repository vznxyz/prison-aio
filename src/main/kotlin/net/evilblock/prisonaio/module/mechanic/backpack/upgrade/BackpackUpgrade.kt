/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.upgrade

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ColorUtil
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.inventory.ItemStack

interface BackpackUpgrade {

    fun getId(): String

    fun getName(): String

    fun getColor(): Color

    fun getChatColor(): ChatColor

    fun getDescription(): String

    fun getIcon(): ItemStack

    fun getCost(level: Int): Long

    fun getMaxLevel(): Int

    fun lorified(): String {
        return "${getChatColor()}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}${getName()}"
    }

}
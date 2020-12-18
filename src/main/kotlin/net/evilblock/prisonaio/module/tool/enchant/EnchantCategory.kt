/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant

import org.bukkit.ChatColor
import org.bukkit.Color

enum class EnchantCategory(
    val displayName: String,
    val textColor: ChatColor,
    val iconColor: Color
) {

    VANILLA("Vanilla", ChatColor.GREEN, Color.LIME),
    WEALTH_LOW("Wealth ${ChatColor.GRAY}(Low)", ChatColor.YELLOW, Color.YELLOW),
    WEALTH_HIGH("Wealth ${ChatColor.GRAY}(High)", ChatColor.AQUA, Color.AQUA),
    DESTRUCTIVE("Destructive", ChatColor.RED, Color.RED),
    EXOTIC("Exotic", ChatColor.LIGHT_PURPLE, Color.FUCHSIA);

    fun getColoredName(): String {
        return textColor.toString() + ChatColor.BOLD.toString() + displayName
    }

}
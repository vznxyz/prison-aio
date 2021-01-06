/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar.structure

import org.bukkit.ChatColor

enum class AvatarElement(
    val readableName: String,
    val nameFormat: String,
    val abilityDescription: String
) {

    AIR(
        readableName = "Air",
        nameFormat = "${ChatColor.WHITE}${ChatColor.BOLD}",
        abilityDescription = "Get access to the Fly perk at all times."
    ),
    WATER(
        readableName = "Water",
        nameFormat = "${ChatColor.AQUA}${ChatColor.BOLD}",
        abilityDescription = ""
    ),
    EARTH(
        readableName = "Earth",
        nameFormat = "${ChatColor.GREEN}${ChatColor.BOLD}",
        abilityDescription = ""
    ),
    FIRE(
        readableName = "Fire",
        nameFormat = "${ChatColor.RED}${ChatColor.BOLD}",
        abilityDescription = ""
    );

    fun getDisplayName(): String {
        return nameFormat + readableName
    }

}
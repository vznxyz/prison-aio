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
    val abilityDescription: String,
    val color: ChatColor,
    val glassColor: Byte,
    val headTexture: String
) {

    AIR(
        readableName = "Air",
        nameFormat = "${ChatColor.WHITE}${ChatColor.BOLD}",
        abilityDescription = "Get access to the Fly perk at all times.",
        color = ChatColor.WHITE,
        glassColor = 0,
        headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZGVjMDRkMjM4MGNkNzcwMjdmOWQ0NDQ1NWM5OGI3ZWRjNWY2NjRjYTBkZDMwYTYxMDY5MDM5MTUzOTFkYiJ9fX0="
    ),
    WATER(
        readableName = "Water",
        nameFormat = "${ChatColor.BLUE}${ChatColor.BOLD}",
        abilityDescription = "",
        color = ChatColor.BLUE,
        glassColor = 3,
        headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQzNzI4NTc5MzEzMWVkNzU1ZjFiMDA5OGYyOWRkNDEzZDY3NjU2YjYyMDg3Mjg5MzU0OTJiNDliMWQwZDRiYSJ9fX0="
    ),
    EARTH(
        readableName = "Earth",
        nameFormat = "${ChatColor.GREEN}${ChatColor.BOLD}",
        abilityDescription = "",
        color = ChatColor.GREEN,
        glassColor = 5,
        headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjNhNThiZWM2NTY2OGI2ODJhYmFiMzYxMzAwYTljNDEzM2JiNmMwNmRiODg0NzIxMGE2MmI4ODRlZTZmYmM3ZCJ9fX0="
    ),
    FIRE(
        readableName = "Fire",
        nameFormat = "${ChatColor.RED}${ChatColor.BOLD}",
        abilityDescription = "",
        color = ChatColor.RED,
        glassColor = 14,
        headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2NiOTQyNjNmNzEyZDkwMmRkMTM2MjUxZmQ0ZDhkMDA1ODkwYzY1N2FiNWVlNDkwY2NjOWJmNmVjMDliOGY1NyJ9fX0="
    );

    fun getDisplayName(): String {
        return nameFormat + readableName
    }

}
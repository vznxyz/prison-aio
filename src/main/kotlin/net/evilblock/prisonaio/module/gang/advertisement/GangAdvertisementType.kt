/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.advertisement

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class GangAdvertisementType(
    val simpleName: String,
    val displayName: String,
    val color: ChatColor,
    val icon: ItemStack,
    val description: String
) {

    PLAYER("player", "Player", ChatColor.AQUA, ItemStack(Material.SKULL_ITEM, 1, 3), "Create an advertisement that you're looking to join a gang."),
    GANG("gang", "Gang", ChatColor.GOLD, ItemStack(Material.BEACON), "Create an advertisement that your gang is looking for members to join.");

    fun getColoredName(): String {
        return color.toString() + ChatColor.BOLD.toString() + displayName
    }

}
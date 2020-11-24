/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.upgrade.impl

import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.BackpackUpgrade
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object CapacityUpgrade : BackpackUpgrade {

    override fun getId(): String {
        return "capacity"
    }

    override fun getName(): String {
        return "Capacity"
    }

    override fun getDescription(): String {
        return "Increases the amount of items that can fit into a backpack."
    }

    override fun getColor(): Color {
        return Color.GREEN
    }

    override fun getChatColor(): ChatColor {
        return ChatColor.GREEN
    }

    override fun getIcon(): ItemStack {
        return ItemStack(Material.STORAGE_MINECART)
    }

    override fun getCost(level: Int): Long {
        return (50 + ((level - 1) * 20)).toLong()
    }

    override fun getMaxLevel(): Int {
        return 10_000
    }

}
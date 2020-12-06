package net.evilblock.prisonaio.module.robot.impl.upgrade

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Upgrade {

    fun getUniqueId(): String

    fun getName(): String

    fun getDescription(): List<String>

    fun getColor(): ChatColor

    fun getColoredName(): String {
        return "${getColor()}${ChatColor.BOLD}${getName()}"
    }

    fun getIcon(): ItemStack

    fun getMaxLevel(): Int

    fun getPrice(player: Player, tier: Int, level: Int): Double

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

class RefundsButton : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.RED}${ChatColor.BOLD}Refund Enchants ${ChatColor.GRAY}${Constants.DOUBLE_ARROW_LEFT}"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf("${ChatColor.GRAY}Click here to refund enchantments")
    }

    override fun getMaterial(player: Player): Material {
        return Material.HOPPER
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        return itemMeta
    }

}
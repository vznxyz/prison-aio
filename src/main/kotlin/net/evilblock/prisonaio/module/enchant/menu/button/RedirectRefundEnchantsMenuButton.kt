/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class RedirectRefundEnchantsMenuButton : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Refund Enchants"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf("${ChatColor.GRAY}Click here to refund enchantments")
    }

    override fun getMaterial(player: Player): Material {
        return Material.STAINED_GLASS_PANE
    }

    override fun getDamageValue(player: Player): Byte {
        return 14
    }

}
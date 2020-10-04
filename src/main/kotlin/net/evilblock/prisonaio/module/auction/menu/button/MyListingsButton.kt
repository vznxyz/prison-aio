/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.button

import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

class MyListingsButton : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}My Listings"
    }

    override fun getDescription(player: Player): List<String> {
        val description = arrayListOf<String>()

        

        return description
    }

    override fun getMaterial(player: Player): Material {
        return Material.SKULL_ITEM
    }

    override fun getDamageValue(player: Player): Byte {
        return 3
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        val meta = itemMeta as SkullMeta
        meta.owner = player.name
        return meta
    }

}
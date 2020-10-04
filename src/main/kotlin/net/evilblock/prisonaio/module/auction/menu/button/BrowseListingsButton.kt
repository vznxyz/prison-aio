/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.button

import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class BrowseListingsButton : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.AQUA}${ChatColor.BOLD}Browse Listings"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf("")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.button

import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.auction.menu.AuctionHouseMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class AllListingsButton : TexturedHeadButton(Constants.IB_GLOBE_TEXTURE) {

    override fun getName(player: Player): String {
        return "${ChatColor.AQUA}${ChatColor.BOLD}All Listings"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also {
            it.add("")
            it.addAll(TextSplitter.split(text = "Shows you all the listings in Auction House, filtered and sorted how you want by using the controls in the top right of the menu."))
            it.add("")
            it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to view all listings")
        }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            AuctionHouseMenu().openMenu(player)
        }
    }

}
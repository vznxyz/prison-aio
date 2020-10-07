/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.auction.listing.AuctionListing
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class BrowseListingsMenu(val listings: List<AuctionListing>) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Browse Auction Listings"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (listing in listings) {
//            buttons[buttons.size] =
        }

        return buttons
    }

    private inner class AuctionListingButton(private val listing: AuctionListing) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val builder = ItemBuilder.copyOf(listing.getGoods())

            val info = arrayListOf<String>()
            info.add("")
            info.add("${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}-----------------------")
            info.add("${ChatColor.YELLOW}${ChatColor.BOLD}Listing Information")
            info.add("")
            info.add("")

            builder.addToLore(*info.toTypedArray())

            return builder.build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {

            }
        }
    }

}
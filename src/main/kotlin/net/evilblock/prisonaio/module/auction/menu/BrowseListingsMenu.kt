/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.auction.listing.AuctionListing
import org.bukkit.entity.Player

class BrowseListingsMenu(val listings: List<AuctionListing>) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Browse Auction Listings"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (listing in listings) {

        }

        return buttons
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu

import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.listing.Listing
import org.bukkit.entity.Player

class AuctionHouseMenu : BrowseListingsMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Auction House"
    }

    override fun getListings(player: Player): Collection<Listing> {
        return AuctionHouseHandler.getAllListings().filter { !it.isCompleted() }
    }

}
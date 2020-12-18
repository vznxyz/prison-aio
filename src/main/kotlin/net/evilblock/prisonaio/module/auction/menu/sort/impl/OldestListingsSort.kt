/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.sort.impl

import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.menu.sort.ListingsSort

object OldestListingsSort : ListingsSort {

    override fun getName(): String {
        return "Oldest"
    }

    override fun apply(listings: List<Listing>): List<Listing> {
        return listings.sortedBy { it.createdAt }
    }

}
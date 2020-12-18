/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.filter.impl

import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.menu.filter.ListingsFilter

object FeaturedListingsFilter : ListingsFilter {

    override fun getName(): String {
        return "Featured"
    }

    override fun apply(listings: Collection<Listing>): List<Listing> {
        return listings.filter { listing -> listing.isFeatured() }
    }

}
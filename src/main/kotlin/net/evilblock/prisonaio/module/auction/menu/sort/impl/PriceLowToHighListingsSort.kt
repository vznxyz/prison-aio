/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.sort.impl

import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.listing.ListingType
import net.evilblock.prisonaio.module.auction.menu.sort.ListingsSort
import java.math.BigInteger

object PriceLowToHighListingsSort : ListingsSort {

    override fun getName(): String {
        return "Price Lowest -> Highest"
    }

    override fun apply(listings: List<Listing>): List<Listing> {
        return listings.sortedBy {
            if (it.listingType == ListingType.AUCTION) {
                if (it.isBINEnabled()) {
                    maxOf(it.getBINPrice(), it.getLatestBid()?.amount ?: BigInteger.ZERO)
                } else {
                    it.getLatestBid()?.amount ?: BigInteger.ZERO
                }
            } else {
                it.getBINPrice()
            }
        }
    }

}
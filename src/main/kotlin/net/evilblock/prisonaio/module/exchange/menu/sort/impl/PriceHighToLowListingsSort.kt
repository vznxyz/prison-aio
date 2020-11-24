/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.sort.impl

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListingType
import net.evilblock.prisonaio.module.exchange.menu.sort.ListingsSort
import java.math.BigInteger

object PriceHighToLowListingsSort : ListingsSort {

    override fun getName(): String {
        return "Price Highest -> Lowest"
    }

    override fun apply(listings: List<GrandExchangeListing>): List<GrandExchangeListing> {
        return listings.sortedBy {
            if (it.listingType == GrandExchangeListingType.AUCTION) {
                if (it.isBINEnabled()) {
                    maxOf(it.getBINPrice(), it.getLatestBid()?.amount ?: BigInteger.ZERO)
                } else {
                    it.getLatestBid()?.amount ?: BigInteger.ZERO
                }
            } else {
                it.getBINPrice()
            }
        }.reversed()
    }

}
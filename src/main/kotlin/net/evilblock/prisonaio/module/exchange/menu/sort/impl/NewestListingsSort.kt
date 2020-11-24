/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.sort.impl

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.menu.sort.ListingsSort

object NewestListingsSort : ListingsSort {

    override fun getName(): String {
        return "Newest"
    }

    override fun apply(listings: List<GrandExchangeListing>): List<GrandExchangeListing> {
        return listings.sortedBy { it.createdAt }.reversed()
    }

}
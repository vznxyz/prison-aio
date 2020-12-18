/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.filter

import net.evilblock.prisonaio.module.auction.listing.Listing

interface ListingsFilter {

    fun getName(): String

    fun apply(listings: Collection<Listing>): List<Listing>

}
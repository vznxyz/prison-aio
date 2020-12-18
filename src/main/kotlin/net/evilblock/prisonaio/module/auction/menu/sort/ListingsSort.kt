/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.sort

import net.evilblock.prisonaio.module.auction.listing.Listing

interface ListingsSort {

    fun getName(): String

    fun apply(listings: List<Listing>): List<Listing>

}
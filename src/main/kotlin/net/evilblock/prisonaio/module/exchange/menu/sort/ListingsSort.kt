/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.sort

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing

interface ListingsSort {

    fun getName(): String

    fun apply(listings: List<GrandExchangeListing>): List<GrandExchangeListing>

}
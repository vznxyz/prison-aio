/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.filter

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing

interface ListingsFilter {

    fun getName(): String

    fun apply(listings: Collection<GrandExchangeListing>): List<GrandExchangeListing>

}
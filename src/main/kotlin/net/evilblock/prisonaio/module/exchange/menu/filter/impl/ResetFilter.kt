/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.filter.impl

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.menu.filter.ListingsFilter

object ResetFilter : ListingsFilter {

    override fun getName(): String {
        return "None"
    }

    override fun apply(listings: Collection<GrandExchangeListing>): List<GrandExchangeListing> {
        throw IllegalStateException("This type of filter cannot be applied")
    }

}
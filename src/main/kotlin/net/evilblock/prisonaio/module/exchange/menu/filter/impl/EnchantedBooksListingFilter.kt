/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.filter.impl

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.menu.filter.ListingsFilter
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager

object EnchantedBooksListingFilter : ListingsFilter {

    override fun getName(): String {
        return "Enchanted Books"
    }

    override fun apply(listings: Collection<GrandExchangeListing>): List<GrandExchangeListing> {
        return listings.filter { listing -> EnchantsManager.isEnchantItem(listing.getGoods()) }
    }

}
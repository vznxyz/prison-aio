/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.filter.impl

import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.menu.filter.ListingsFilter
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler

object EnchantedBooksListingFilter : ListingsFilter {

    override fun getName(): String {
        return "Enchanted Books"
    }

    override fun apply(listings: Collection<Listing>): List<Listing> {
        return listings.filter { listing -> EnchantHandler.isEnchantItem(listing.getGoods()) }
    }

}
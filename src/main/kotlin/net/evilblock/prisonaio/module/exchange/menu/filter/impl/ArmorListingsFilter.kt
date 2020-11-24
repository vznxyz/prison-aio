/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.filter.impl

import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.menu.filter.ListingsFilter
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler

object ArmorListingsFilter : ListingsFilter {

    override fun getName(): String {
        return "Armor"
    }

    override fun apply(listings: Collection<GrandExchangeListing>): List<GrandExchangeListing> {
        return listings.filter { listing -> AbilityArmorHandler.isArmorPiece(listing.getGoods()) }
    }

}
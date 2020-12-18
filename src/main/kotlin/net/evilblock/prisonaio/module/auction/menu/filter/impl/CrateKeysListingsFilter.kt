/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.filter.impl

import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.menu.filter.ListingsFilter

object CrateKeysListingsFilter : ListingsFilter {

    override fun getName(): String {
        return "Crate Keys"
    }

    override fun apply(listings: Collection<Listing>): List<Listing> {
        return listings.filter { listing ->
            val goods = listing.getGoods()
            return@filter goods.hasItemMeta() && goods.itemMeta.hasLore() && net.evilblock.cubed.util.bukkit.HiddenLore.hasHiddenString(goods.itemMeta.lore.first())
        }
    }

}
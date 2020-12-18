/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.filter.impl

import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.menu.filter.ListingsFilter
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler

object PickaxesListingsFilter : ListingsFilter {

    override fun getName(): String {
        return "Pickaxes"
    }

    override fun apply(listings: Collection<Listing>): List<Listing> {
        return listings.filter { listing -> PickaxeHandler.getPickaxeData(listing.getGoods()) != null || MechanicsModule.isPickaxe(listing.getGoods()) }
    }

}
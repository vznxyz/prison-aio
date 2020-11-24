/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu

import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import org.bukkit.entity.Player

class AllListingsMenu : BrowseListingsMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "The Grand Exchange"
    }

    override fun getListings(player: Player): Collection<GrandExchangeListing> {
        return GrandExchangeHandler.getAllListings().filter { !it.isCompleted() }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.menu.button.AllListingsButton
import net.evilblock.prisonaio.module.user.User
import org.bukkit.entity.Player

class UserListingsMenu(private val user: User) : BrowseListingsMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return if (player.uniqueId == user.uuid) {
            "My Listings"
        } else {
            "${user.getUsername()}'s Listings"
        }
    }

    override fun getListings(player: Player): Collection<GrandExchangeListing> {
        return GrandExchangeHandler.getPlayerListings(user.uuid).sortedBy { it.createdAt }
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return (super.getGlobalButtons(player) as MutableMap).also {
            it[2] = AllListingsButton()
        }
    }

}
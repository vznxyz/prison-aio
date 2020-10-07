/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.auction.menu.BrowseListingsMenu
import org.bukkit.entity.Player

object AuctionHouseCommand {

    @Command(
        names = ["auctionhouse", "auction", "ah"],
        description = "Browse the Auction House"
    )
    @JvmStatic
    fun execute(player: Player) {
//        BrowseListingsMenu().openMenu(player)
    }

}
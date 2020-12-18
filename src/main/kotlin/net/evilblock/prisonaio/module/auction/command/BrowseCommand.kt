/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.auction.menu.AuctionHouseMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object BrowseCommand {

    @Command(
        names = ["auction-house", "ah", "auctionhouse", "auction"],
        description = "Browse the Auction House"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't open menus while your combat timer is active!")
            return
        }

        AuctionHouseMenu().openMenu(player)
    }

}
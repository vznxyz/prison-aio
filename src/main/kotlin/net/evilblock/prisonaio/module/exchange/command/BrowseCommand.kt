/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.menu.AllListingsMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object BrowseCommand {

    @Command(
        names = ["grand-exchange", "ge", "grandexchange", "exchange", "auctionhouse", "auction", "ah"],
        description = "Browse the Grand Exchange"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't view the Grand Exchange while in combat!")
            return
        }

        AllListingsMenu().openMenu(player)
    }

}
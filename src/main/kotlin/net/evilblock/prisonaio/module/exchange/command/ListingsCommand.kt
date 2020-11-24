/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.exchange.menu.UserListingsMenu
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ListingsCommand {

    @Command(
        names = ["grand-exchange list", "ge list", "grandexchange list", "auctionhouse list", "auction list", "ah list"],
        description = "Browse a player's listings on the Grand Exchange"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") target: User) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't view the Grand Exchange while in combat!")
            return
        }

        UserListingsMenu(target).openMenu(player)
    }

}
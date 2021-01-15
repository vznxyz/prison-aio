/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.bounty.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.bounty.menu.BountiesMenu
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object BountiesCommand {

    @Command(
        names = ["bounties"],
        description = "View all the bounties placed on players' heads"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't open menus while your combat timer is active!")
            return
        }

        BountiesMenu().openMenu(player)
    }

}
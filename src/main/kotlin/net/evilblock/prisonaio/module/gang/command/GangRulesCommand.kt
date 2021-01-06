/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.gang.rules.menu.RulesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangRulesCommand {

    @Command(
        names = ["gangs rules", "gang rules"],
        description = "Opens the Gang Rules menu"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't open menus while your combat timer is active!")
            return
        }

        RulesMenu().openMenu(player)
    }

}
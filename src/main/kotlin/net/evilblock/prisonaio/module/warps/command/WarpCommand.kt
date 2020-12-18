/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warps.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.warps.Warp
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object WarpCommand {

    @Command(
        names = ["warp"],
        description = "Teleport to a warp"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "warp") warp: Warp) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't warp while your combat timer is active!")
            return
        }

        if (warp.isPriceSet()) {
            if (warp.canAfford(player)) {
                warp.teleport(player)
            }
        } else {
            warp.teleport(player)
        }
    }

}
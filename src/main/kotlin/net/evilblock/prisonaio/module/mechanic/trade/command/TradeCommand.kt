/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.mechanic.trade.TradeHandler
import net.evilblock.prisonaio.module.mechanic.trade.TradeRequest
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object TradeCommand {

    @Command(
        names = ["trade", "exchange"],
        description = "Start a trade with another player"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") target: Player) {
        if (TradeHandler.disabled) {
            sender.sendMessage("${ChatColor.RED}Trading is currently disabled!")
            return
        }

        if (TradeHandler.hasPendingRequestFrom(sender, target)) {
            sender.sendMessage("${ChatColor.RED}${target.name} already has a pending trade request from you!")
            return
        }

        if (CombatTimerHandler.isOnTimer(sender)) {
            sender.sendMessage("${ChatColor.RED}You must wait until you are out of combat to send a trade request!")
            return
        }

        if (CombatTimerHandler.isOnTimer(target)) {
            sender.sendMessage("${ChatColor.RED}${target.name} is currently in combat!")
            return
        }

        val request = TradeRequest(sender, target)
        TradeHandler.trackPendingRequest(request)

        request.send()
    }

}
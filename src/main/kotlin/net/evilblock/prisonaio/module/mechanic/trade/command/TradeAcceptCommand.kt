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
import net.evilblock.prisonaio.module.mechanic.trade.Trade
import net.evilblock.prisonaio.module.mechanic.trade.TradeHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object TradeAcceptCommand {

    @Command(
        names = ["trade accept"],
        description = "Accept an other player's trade request"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") target: Player) {
        if (TradeHandler.disabled) {
            sender.sendMessage("${ChatColor.RED}Trading is currently disabled!")
            return
        }

        if (!TradeHandler.hasPendingRequestFrom(target, sender)) {
            sender.sendMessage("${ChatColor.RED}You don't have a pending trade request from ${target.name}!")
            return
        }

        if (CombatTimerHandler.isOnTimer(sender)) {
            sender.sendMessage("${ChatColor.RED}You must wait until you are out of combat to accept a trade request!")
            return
        }

        if (CombatTimerHandler.isOnTimer(target)) {
            sender.sendMessage("${ChatColor.RED}${target.name} is currently in combat!")
            return
        }

        if (TradeHandler.getActiveTrade(target) != null) {
            sender.sendMessage("${ChatColor.RED}${target.name} is currently trading with another player right now!")
            return
        }

        TradeHandler.forgetPendingRequest(target, sender)

        val trade = Trade(target, sender)
        TradeHandler.trackActiveTrade(trade)

        trade.start()
    }

}
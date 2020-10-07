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

object TradeDeclineCommand {

    @Command(
        names = ["trade decline"],
        description = "Decline an other player's trade request"
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

        TradeHandler.getPendingRequest(target, sender)?.let {
            TradeHandler.forgetPendingRequest(target, sender)
            it.decline()
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade

import mkremins.fanciful.FancyMessage
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class TradeRequest(val sender: Player, val target: Player) {

    val createdAt: Long = System.currentTimeMillis()

    fun send() {
        sender.sendMessage("${ChatColor.GRAY}You sent a trade request to ${Formats.formatPlayer(target)}${ChatColor.GRAY}!")

        FancyMessage("${ChatColor.GRAY}New trade request from ${Formats.formatPlayer(sender)}${ChatColor.GRAY}.")
            .then(" ")
            .then("${ChatColor.GRAY}[")
            .then("${ChatColor.GREEN}${ChatColor.BOLD}ACCEPT")
            .command("/trade accept ${sender.name}")
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to accept the trade request from ${sender.name}."))
            .then("${ChatColor.GRAY}]")
            .then(" ")
            .then("${ChatColor.GRAY}[")
            .then("${ChatColor.RED}${ChatColor.BOLD}DECLINE")
            .command("/trade decline ${sender.name}")
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to decline the trade request from ${sender.name}."))
            .then("${ChatColor.GRAY}]")
            .send(target)
    }

    fun decline() {
        sender.sendMessage("${ChatColor.RED}${target.name} has declined your request to trade.")
        target.sendMessage("${ChatColor.RED}You've declined ${sender.name}'s request to trade.")
    }

    fun expired() {
        sender.sendMessage("${ChatColor.RED}Your trade request to ${target.name} has expired!")
        target.sendMessage("${ChatColor.RED}The trade request from ${sender.name} has expired!")
    }

}
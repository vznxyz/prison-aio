/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.leaderboard.impl.MoneyBalanceLeaderboard
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import kotlin.math.ceil

object BalanceTopCommand {

    @Command(
        names = ["baltop", "balancetop", "topbal"],
        description = "Displays the players with the most balance"
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "page", defaultValue = "1") page: Int) {
        val maxPages = ceil(MoneyBalanceLeaderboard.entries.size.coerceAtLeast(1) / 20.0).toInt()

        if (page < 0 || page > maxPages) {
            sender.sendMessage("${ChatColor.RED}Page not found!")
            return
        }

        sender.sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}Balance Top ${ChatColor.GRAY}(Page ${page}/${maxPages})")

        val endRange = (page * 10) - 1
        val startRange = endRange - 9

        for (i in startRange..endRange) {
            if (i >= MoneyBalanceLeaderboard.entries.size) {
                break
            }

            val entry = MoneyBalanceLeaderboard.entries[i]
            sender.sendMessage("${ChatColor.GRAY}${i + 1}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${Formats.formatMoney(entry.value as BigDecimal)}")
        }
    }

}
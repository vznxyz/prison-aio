/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.rank.RanksModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RankScaleCommand {

    @Command(
        names = ["prison rank scale"],
        description = "Shows the rank price multiplier by increasing prestige",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        for ((prestige, multiplier) in RanksModule.getPrestigeRankPriceMultipliers().toSortedMap(Comparator.comparingInt { it })) {
            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Prestige $prestige ${ChatColor.GRAY}-> ${ChatColor.BOLD}$multiplier")
        }
    }

}
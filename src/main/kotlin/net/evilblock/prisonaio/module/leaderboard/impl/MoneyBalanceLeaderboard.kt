/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import java.math.BigDecimal
import java.util.*

object MoneyBalanceLeaderboard : Leaderboard("top-balance", "${ChatColor.GREEN}${ChatColor.BOLD}Top Balance") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<BigDecimal>>()

        for (document in UserHandler.getCollection().find()) {
            val uuid = UUID.fromString(document.getString("uuid"))

            if (LeaderboardsModule.exemptions.contains(uuid)) {
                continue
            }

            val moneyBalance = when (val rawValue = document["moneyBalance"]) {
                is String -> {
                    BigDecimal(rawValue)
                }
                is Double -> {
                    BigDecimal(rawValue)
                }
                is Long -> {
                    BigDecimal(rawValue)
                }
                else -> {
                    BigDecimal(rawValue as Int)
                }
            }

            val displayName = Cubed.instance.uuidCache.name(uuid)

            entries.add(LeaderboardEntry(0, displayName, displayName, moneyBalance))
        }

        return entries.sortedByDescending { it.value }
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${Formats.formatMoney(entry.value as BigDecimal)}"
    }

}
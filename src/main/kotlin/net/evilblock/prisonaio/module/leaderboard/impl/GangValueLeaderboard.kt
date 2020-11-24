/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import java.math.BigInteger

object GangValueLeaderboard : Leaderboard("gang-value", "${ChatColor.DARK_RED}${ChatColor.BOLD}Top Gang Value") {

    private val LONG_MAX_BIG_INT = BigInteger.valueOf(Long.MAX_VALUE)

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<BigInteger>>()

        for (gang in GangHandler.getAllGangs()) {
            val skinSource = Cubed.instance.uuidCache.name(gang.leader)
            entries.add(LeaderboardEntry(0, gang.name, skinSource, gang.cachedValue))
        }

        return entries.sortedByDescending { it.value }.take(CACHED_ENTRIES_SIZE)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        val value = entry.value as BigInteger
        return if (value > LONG_MAX_BIG_INT) {
            "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}$value"
        } else {
            "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${Formats.formatMoney((entry.value.longValueExact()).toDouble())}"
        }
    }

}
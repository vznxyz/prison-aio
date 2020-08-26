/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor

object GangValueLeaderboard : Leaderboard("gang-value", "${ChatColor.DARK_RED}${ChatColor.BOLD}Top Gang Value") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Long>>()

        for (gang in GangHandler.getAllGangs()) {
            entries.add(LeaderboardEntry(0, gang.name, gang.cachedCellValue))
        }

        return entries.sortedByDescending { it.value }.take(CACHED_ENTRIES_SIZE)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${Formats.formatMoney((entry.value as Long).toDouble())}"
    }

}
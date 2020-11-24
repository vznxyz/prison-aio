/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import org.bukkit.ChatColor

object GangTrophiesLeaderboard : Leaderboard("gang-trophies", "${ChatColor.DARK_RED}${ChatColor.BOLD}Top Gang Trophies") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Int>>()

        for (gang in GangHandler.getAllGangs()) {
            val skinSource = Cubed.instance.uuidCache.name(gang.leader)
            entries.add(LeaderboardEntry(0, gang.name, skinSource, gang.getTrophies()))
        }

        return entries.sortedByDescending { it.value }.take(CACHED_ENTRIES_SIZE)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.format(entry.value as Int)} Trophies"
    }

}
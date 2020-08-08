/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import org.bukkit.ChatColor

object GangTopLeaderboard : Leaderboard("gang-top", "Gang Top") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Int>>()

        for (gang in GangHandler.getAllGangs()) {
            entries.add(LeaderboardEntry(0, gang.name, gang.getTrophies()))
        }

        return entries.sortedByDescending { it.value }.take(5)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.format(entry.value as Int)} Trophies"
    }

}
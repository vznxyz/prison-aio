/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bson.Document
import org.bukkit.ChatColor
import java.util.*

object TopTimePlayedLeaderboard : Leaderboard("top-time-played", "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Top Time Played") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Long>>()

        for (document in UserHandler.getCollection().find()) {
            val uuid = UUID.fromString(document.getString("uuid"))

            if (LeaderboardsModule.exemptions.contains(uuid)) {
                continue
            }

            val displayName = Cubed.instance.uuidCache.name(uuid)
            val statistics = document["statistics"] ?: continue

            val playTime = (statistics as Document)["playTime"]
            if (playTime is Int) {
                entries.add(LeaderboardEntry(0, displayName, displayName, playTime.toLong()))
            } else {
                entries.add(LeaderboardEntry(0, displayName, displayName, playTime as Long))
            }
        }

        return entries.sortedByDescending { it.value }.take(CACHED_ENTRIES_SIZE)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.LIGHT_PURPLE}${TimeUtil.formatIntoAbbreviatedString(((entry.value as Long) / 1000.0).toInt())}"
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bson.Document
import org.bukkit.ChatColor
import java.util.*

object BlocksMinedLeaderboard : Leaderboard("blocks-progress", "${ChatColor.AQUA}${ChatColor.BOLD}Top Blocks Mined") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Int>>()

        for (document in UserHandler.getCollection().find()) {
            val uuid = UUID.fromString(document.getString("uuid"))

            if (LeaderboardsModule.exemptions.contains(uuid)) {
                continue
            }

            val displayName = Cubed.instance.uuidCache.name(uuid)
            val blocksMined = (document["statistics"] as Document).getInteger("blocksMined")

            entries.add(LeaderboardEntry(0, displayName, displayName, blocksMined))
        }

        return entries.sortedByDescending { it.value }.take(CACHED_ENTRIES_SIZE)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.AQUA}${NumberUtils.format((entry.value as Int).toLong())}"
    }

}
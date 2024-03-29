/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import java.util.*

object TopTokensLeaderboard : Leaderboard("top-tokens", "${ChatColor.GOLD}${ChatColor.BOLD}Top Tokens") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Long>>()

        for (document in UserHandler.getCollection().find()) {
            val uuid = UUID.fromString(document.getString("uuid"))

            if (LeaderboardsModule.exemptions.contains(uuid)) {
                continue
            }

            val displayName = Cubed.instance.uuidCache.name(uuid)

            val balance = document["tokenBalance"] ?: document["tokensBalance"]
            if (balance is Int) {
                entries.add(LeaderboardEntry(0, displayName, displayName, balance.toLong()))
            } else {
                entries.add(LeaderboardEntry(0, displayName, displayName, balance as Long))
            }
        }

        return entries.sortedByDescending { it.value }.take(CACHED_ENTRIES_SIZE)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.RED}${Constants.TOKENS_SYMBOL}${ChatColor.GOLD}${NumberUtils.format(entry.value as Long)}"
    }

}
package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Constants
import org.bson.Document
import org.bukkit.ChatColor
import java.util.*

object BlocksMinedLeaderboard : Leaderboard("blocks-mined", "Top Blocks Mined") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Int>>()

        for (document in UserHandler.getCollection().find()) {
            val uuid = UUID.fromString(document.getString("uuid"))

            val blocksMined = (document["statistics"] as Document).getInteger("blocksMined")
            entries.add(LeaderboardEntry(0, Cubed.instance.uuidCache.name(uuid), blocksMined))
        }

        return entries.sortedByDescending { it.value }.take(5)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.RED}${Constants.TOKENS_SYMBOL}${ChatColor.GOLD}${NumberUtils.format(entry.value as Long)}"
    }

}
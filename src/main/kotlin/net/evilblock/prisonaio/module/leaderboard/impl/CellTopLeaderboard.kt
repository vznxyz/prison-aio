package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import org.bukkit.ChatColor

object CellTopLeaderboard : Leaderboard("cell-top", "Cell Top") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Long>>()

        for (cell in CellHandler.getAllCells()) {
            entries.add(LeaderboardEntry(0, cell.name, cell.cachedCellValue))
        }

        return entries.sortedByDescending { it.value }.take(5)
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.AQUA}${ChatColor.BOLD}$${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(entry.value as Long)}"
    }

}
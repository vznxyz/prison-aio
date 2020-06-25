/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardEntry
import org.bukkit.Bukkit
import org.bukkit.ChatColor

object MoneyBalanceLeaderboard : Leaderboard("top-balance", "Top Balance") {

    override fun fetchEntries(): List<LeaderboardEntry<*>> {
        val entries = arrayListOf<LeaderboardEntry<Double>>()

        VaultHook.useEconomy { economy ->
            entries.addAll(
                Cubed.instance.uuidCache.getCachedUuids()
                    .asSequence()
                    .mapNotNull { Bukkit.getOfflinePlayer(it) }
                    .filter { it.name != null }
                    .map { LeaderboardEntry(0, Cubed.instance.uuidCache.name(it.uniqueId), economy.getBalance(it)) }
                    .sortedByDescending { it.value }
                    .take(5)
                    .toList()
            )
        }

        return entries
    }

    override fun formatEntry(entry: LeaderboardEntry<*>): String {
        return "${ChatColor.GRAY}${entry.position}. ${ChatColor.YELLOW}${entry.displayName} ${ChatColor.GRAY}- ${ChatColor.AQUA}${ChatColor.BOLD}$${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(entry.value as Double)}"
    }

}
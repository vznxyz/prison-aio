/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge.impl

import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.challenge.GangChallenge
import org.bukkit.ChatColor

class LeaderboardsPlacementGangChallenge(id: String, reward: Int) : GangChallenge(id, reward) {

    override fun getRenderedName(): String {
        return "Gang Top Leaderboards Placement"
    }

    override fun renderGoal(): List<String> {
        return listOf(
            "${ChatColor.GRAY}You and your gang members need to",
            "${ChatColor.GRAY}acquire trophies and secure a position",
            "${ChatColor.GRAY}on the Gang Top leaderboards."
        )
    }

    override fun isProgressive(): Boolean {
        return false
    }

    override fun meetsCompletionRequirements(gang: Gang): Boolean {
        return gang.challengesData.placedLeaderboards
    }

}
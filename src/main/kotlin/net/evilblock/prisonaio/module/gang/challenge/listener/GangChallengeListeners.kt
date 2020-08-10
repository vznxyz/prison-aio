/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.challenge.GangChallengeHandler
import net.evilblock.prisonaio.module.leaderboard.event.LeaderboardsRefreshedEvent
import net.evilblock.prisonaio.module.leaderboard.impl.GangTopLeaderboard
import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object GangChallengeListeners : Listener {

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val gang = GangHandler.getAssumedGang(event.player.uniqueId)
        if (gang != null) {
            gang.challengesData.blocksMined += 1

            Tasks.async {
                GangChallengeHandler.checkCompletions(gang)
            }
        }
    }

    @EventHandler
    fun onAsyncPlayerPrestigeEvent(event: AsyncPlayerPrestigeEvent) {
        val gang = GangHandler.getAssumedGang(event.player.uniqueId)
        if (gang != null) {
            gang.challengesData.acquiredPrestiges += event.to - event.from

            Tasks.async {
                GangChallengeHandler.checkCompletions(gang)
            }
        }
    }

    @EventHandler
    fun onLeaderboardsRefreshedEvent(event: LeaderboardsRefreshedEvent) {
        val gangs = GangTopLeaderboard.entries.mapNotNull { GangHandler.getGangByName(it.displayName) }
        for (gang in gangs) {
            gang.challengesData.placedLeaderboards = true

            Tasks.async {
                GangChallengeHandler.checkCompletions(gang)
            }
        }
    }

}
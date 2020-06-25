/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.challenge.impl.*
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.event.AsyncPlayTimeSyncEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object ChallengeCompletionListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        Tasks.async {
            val user = UserHandler.getUser(event.player.uniqueId)
            for (challenge in ChallengeHandler.getChallenges()) {
                if (challenge.daily) {
                    continue
                }

                if (!user.battlePassData.isPremium()) {
                    continue
                }

                if (user.battlePassData.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge is ExecuteCommandChallenge) {
                    val command = challenge.command
                    if (event.message.startsWith("/$command", ignoreCase = true)) {
                        challenge.onComplete(event.player, user)
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayTimeSyncEvent(event: AsyncPlayTimeSyncEvent) {
        for (challenge in ChallengeHandler.getChallenges()) {
            if (challenge.daily) {
                continue
            }

            if (!event.user.battlePassData.isPremium()) {
                continue
            }

            if (event.user.battlePassData.hasCompletedChallenge(challenge)) {
                continue
            }

            if (challenge is PlayTimeChallenge) {
                if (event.user.statistics.getPlayTime() + event.offset >= challenge.duration) {
                    val player = event.user.getPlayer()
                    if (player != null) {
                        challenge.onComplete(player, event.user)
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayerPrestigeEvent(event: AsyncPlayerPrestigeEvent) {
        for (challenge in ChallengeHandler.getChallenges()) {
            if (challenge.daily) {
                continue
            }

            if (!event.user.battlePassData.isPremium()) {
                continue
            }

            if (event.user.battlePassData.hasCompletedChallenge(challenge)) {
                continue
            }

            if (challenge is PrestigeChallenge) {
                if (event.to >= challenge.prestige) {
                    challenge.onComplete(event.player, event.user)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        Tasks.async {
            val user = UserHandler.getUser(event.player.uniqueId)

            for (challenge in ChallengeHandler.getChallenges()) {
                if (challenge.daily) {
                    continue
                }

                if (!user.battlePassData.isPremium()) {
                    continue
                }

                if (user.battlePassData.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge is BlocksMinedChallenge) {
                    if (user.statistics.getBlocksMined() >= challenge.blocksMined) {
                        challenge.onComplete(event.player, user)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMineBlockBreakEvent(event: MineBlockBreakEvent) {
        Tasks.async {
            val user = UserHandler.getUser(event.player.uniqueId)

            for (challenge in ChallengeHandler.getChallenges()) {
                if (challenge.daily) {
                    continue
                }

                if (!user.battlePassData.isPremium()) {
                    continue
                }

                if (user.battlePassData.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge is BlocksMinedAtMineChallenge) {
                    if (user.statistics.getBlocksMinedAtMine(event.mine) >= challenge.blocksMined) {
                        challenge.onComplete(event.player, user)
                    }
                }
            }
        }
    }

}
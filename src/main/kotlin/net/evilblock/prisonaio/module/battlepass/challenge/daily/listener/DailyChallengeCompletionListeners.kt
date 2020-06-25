/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.daily.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.battlepass.challenge.impl.*
import net.evilblock.prisonaio.module.battlepass.challenge.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.event.AsyncPlayTimeSyncEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object DailyChallengeCompletionListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        Tasks.async {
            val progress = DailyChallengeHandler.getSession().getProgress(event.player.uniqueId)
            for (challenge in DailyChallengeHandler.getSession().getChallenges()) {
                if (progress.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge is ExecuteCommandChallenge) {
                    val command = challenge.command
                    if (event.message.startsWith("/$command", ignoreCase = true)) {
                        challenge.onComplete(event.player, UserHandler.getUser(event.player.uniqueId))
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayTimeSyncEvent(event: AsyncPlayTimeSyncEvent) {
        val progress = DailyChallengeHandler.getSession().getProgress(event.user.uuid)
        progress.addPlayTime(event.offset)

        for (challenge in DailyChallengeHandler.getSession().getChallenges()) {
            if (progress.hasCompletedChallenge(challenge)) {
                continue
            }

            if (challenge is PlayTimeChallenge) {
                if (progress.getPlayTime() >= challenge.duration) {
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
        val progress = DailyChallengeHandler.getSession().getProgress(event.user.uuid)
        progress.addTimePrestiged()

        for (challenge in DailyChallengeHandler.getSession().getChallenges()) {
            if (progress.hasCompletedChallenge(challenge)) {
                continue
            }

            if (challenge is PrestigeChallenge) {
                if (progress.getTimesPrestiged() >= challenge.prestige) {
                    challenge.onComplete(event.player, event.user)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        Tasks.async {
            val progress = DailyChallengeHandler.getSession().getProgress(event.player.uniqueId)
            progress.addBlocksMined(1)

            for (challenge in DailyChallengeHandler.getSession().getChallenges()) {
                if (progress.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge is BlocksMinedChallenge) {
                    if (progress.getBlocksMined() >= challenge.blocksMined) {
                        challenge.onComplete(event.player, UserHandler.getUser(event.player.uniqueId))
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMineBlockBreakEvent(event: MineBlockBreakEvent) {
        Tasks.async {
            val progress = DailyChallengeHandler.getSession().getProgress(event.player.uniqueId)
            progress.addBlocksMinedAtMine(event.mine, 1)

            for (challenge in DailyChallengeHandler.getSession().getChallenges()) {
                if (progress.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge is BlocksMinedAtMineChallenge) {
                    if (progress.getBlocksMinedAtMine(event.mine) >= challenge.blocksMined) {
                        challenge.onComplete(event.player, UserHandler.getUser(event.player.uniqueId))
                    }
                }
            }
        }
    }

}
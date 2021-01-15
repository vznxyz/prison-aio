/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.listener

import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.challenge.impl.ExecuteCommandChallenge
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.combat.event.PlayerKilledEvent
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.event.AsyncPlayTimeSyncEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object ChallengeListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        for (challenge in ChallengeHandler.getAllChallenges()) {
            if (challenge is ExecuteCommandChallenge) {
                if (event.message.startsWith("/${challenge.command}", ignoreCase = true)) {
                    DailyChallengeHandler.getSession().getProgress(event.player.uniqueId).executedCommand(challenge.command)

                    val user = UserHandler.getUser(event.player.uniqueId)
                    user.battlePassProgress.executedCommand(challenge.command)

                    break
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayTimeSyncEvent(event: AsyncPlayTimeSyncEvent) {
        val progress = DailyChallengeHandler.getSession().getProgress(event.user.uuid)
        progress.addPlayTime(event.offset)

        event.user.battlePassProgress.requiresCheck = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayerPrestigeEvent(event: AsyncPlayerPrestigeEvent) {
        val progress = DailyChallengeHandler.getSession().getProgress(event.user.uuid)
        progress.incrementTimePrestiged()

        event.user.battlePassProgress.requiresCheck = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val progress = DailyChallengeHandler.getSession().getProgress(event.player.uniqueId)
        progress.addBlocksMined(1)

        UserHandler.getUser(event.player).battlePassProgress.requiresCheck = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMineBlockBreakEvent(event: MineBlockBreakEvent) {
        val progress = DailyChallengeHandler.getSession().getProgress(event.player.uniqueId)
        progress.addBlocksMinedAtMine(event.mine, 1)

        UserHandler.getUser(event.player).battlePassProgress.requiresCheck = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDeathEvent(event: PlayerKilledEvent) {
        val progress = DailyChallengeHandler.getSession().getProgress(event.killer.uniqueId)
        progress.addKills(1)

        UserHandler.getUser(event.killer).battlePassProgress.requiresCheck = true
    }


}
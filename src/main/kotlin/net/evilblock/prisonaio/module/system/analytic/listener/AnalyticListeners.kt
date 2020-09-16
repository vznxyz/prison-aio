/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.analytic.listener

import net.evilblock.prisonaio.module.system.analytic.Analytic
import net.evilblock.prisonaio.module.user.event.AsyncPlayTimeSyncEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerJoinEvent

object AnalyticListeners : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (!event.player.hasPlayedBefore()) {
            Analytic.UNIQUE_JOINS.updateValue(Analytic.UNIQUE_JOINS.getValue<Int>() + 1)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        Analytic.BLOCKS_MINED.updateValue(Analytic.BLOCKS_MINED.getValue<Int>() + 1)
    }

    @EventHandler
    fun onPlayTimeSyncEvent(event: AsyncPlayTimeSyncEvent) {
        Analytic.TIME_PLAYED.updateValue(Analytic.TIME_PLAYED.getValue<Long>() + event.offset)
    }

}
package net.evilblock.prisonaio.module.environment.analytic.listener

import net.evilblock.prisonaio.module.environment.analytic.Analytic
import net.evilblock.prisonaio.module.user.event.PlayTimeSyncEvent
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
        Analytic.BLOCKS_MINED.updateValue(Analytic.BLOCKS_MINED.getValue<Int>() + 0)
    }

    @EventHandler
    fun onPlayTimeSyncEvent(event: PlayTimeSyncEvent) {
        Analytic.TIME_PLAYED.updateValue(Analytic.TIME_PLAYED.getValue<Long>() + event.offset)
    }

}
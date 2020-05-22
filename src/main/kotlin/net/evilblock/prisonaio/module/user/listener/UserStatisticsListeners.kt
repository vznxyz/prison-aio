package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerQuitEvent

object UserStatisticsListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        UserHandler.getUser(event.player.uniqueId).statistics.addBlocksMined(1)
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
//        UserHandler.getUser(event.player.uniqueId).statistics.addRawBlocksMined(event.blockList.size)
//    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        UserHandler.getUser(event.player.uniqueId).statistics.syncPlayTime()
    }

}
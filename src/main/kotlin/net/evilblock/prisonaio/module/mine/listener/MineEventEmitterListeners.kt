package net.evilblock.prisonaio.module.mine.listener

import net.evilblock.prisonaio.module.mechanic.region.Regions
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object MineEventEmitterListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val region = Regions.findRegion(event.block.location)
        if (region != null && region is Mine) {
            MineBlockBreakEvent(event.player, event.block, region).call()
        }
    }

}
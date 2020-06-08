package net.evilblock.prisonaio.module.mine.listener

import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object MinePercentageListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val optionalMine = MineHandler.getMineByLocation(event.block.location)
        if (optionalMine.isPresent) {
            optionalMine.get().blocksRemaining -= 1
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        for (block in event.blockList) {
            val optionalMine = MineHandler.getMineByLocation(block.location)
            if (optionalMine.isPresent) {
                optionalMine.get().blocksRemaining -= 1
            }
        }
    }

}
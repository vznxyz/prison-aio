package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockRedstoneEvent

object DisableRedstoneListeners : Listener {

    @EventHandler
    fun onRedstoneEvent(event: BlockRedstoneEvent) {
        event.newCurrent = 0
    }

    @EventHandler
    fun onBlockPistonExtendEvent(event: BlockPistonRetractEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockPistonExtendEvent(event: BlockPistonExtendEvent) {
        event.isCancelled = true
    }

}
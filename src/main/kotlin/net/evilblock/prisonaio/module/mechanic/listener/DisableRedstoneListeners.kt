/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockRedstoneEvent

object DisableRedstoneListeners : Listener {

    @EventHandler
    fun onRedstoneEvent(event: BlockRedstoneEvent) {
        if (MechanicsModule.isRedstoneDisabled()) {
            event.newCurrent = 0
        }
    }

    @EventHandler
    fun onBlockPistonExtendEvent(event: BlockPistonRetractEvent) {
        if (MechanicsModule.isRedstoneDisabled()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPistonExtendEvent(event: BlockPistonExtendEvent) {
        if (MechanicsModule.isRedstoneDisabled()) {
            event.isCancelled = true
        }
    }

}
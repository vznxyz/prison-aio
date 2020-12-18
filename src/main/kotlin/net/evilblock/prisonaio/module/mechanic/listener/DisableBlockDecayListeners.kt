/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFadeEvent

/**
 * Prevents block decay for leaves, snow, etc.
 */
object DisableBlockDecayListeners : Listener {

    @EventHandler
    fun onBlockFadeEvent(event: BlockFadeEvent) {
        if (event.block.type == Material.SNOW) {
            event.isCancelled = true
        } else if (event.block.type == Material.LEAVES || event.block.type == Material.LEAVES_2) {
            event.isCancelled = true
        }
    }

}
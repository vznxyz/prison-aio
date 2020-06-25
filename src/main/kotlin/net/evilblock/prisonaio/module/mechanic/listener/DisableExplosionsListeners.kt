/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.Material
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.EntityExplodeEvent

object DisableExplosionsListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBlockIgniteEvent(event: BlockIgniteEvent) {
        if (event.block.type == Material.TNT) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        if (event.entity is TNTPrimed) {
            event.isCancelled = true
        }
    }

}
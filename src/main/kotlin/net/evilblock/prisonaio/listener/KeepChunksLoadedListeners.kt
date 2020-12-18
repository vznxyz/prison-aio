/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent

object KeepChunksLoadedListeners : Listener {

    @EventHandler
    fun onChunkUnloadEvent(event: ChunkUnloadEvent) {
        event.isCancelled = true
    }

}
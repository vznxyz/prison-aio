/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.listener

import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent

object MineChunkListeners : Listener {

    /**
     * Prevents chunks from being unloaded if they are in the bounds of a [Mine].
     */
    @EventHandler
    fun onChunkUnloadEvent(event: ChunkUnloadEvent) {
        for (mine in MineHandler.getMines()) {
            if (mine.cachedChunks.contains(event.chunk)) {
                event.isCancelled = true
            }
        }
    }

}
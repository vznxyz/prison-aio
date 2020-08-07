/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.world.WorldSaveEvent

object PrivateMineWorldListeners : Listener {

    /**
     * Calls [PrivateMineHandler.saveGrid] when the grid world is saved.
     */
    @EventHandler
    fun onWorldSaveEvent(event: WorldSaveEvent) {
        if (event.world == PrivateMineHandler.getGridWorld()) {
            Tasks.async {
                PrivateMineHandler.saveGrid()
            }
        }
    }

    /**
     * Prevents natural creature spawns in the grid world.
     */
    @EventHandler
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        if (event.entity.world == PrivateMineHandler.getGridWorld()) {
            when (event.spawnReason) {
                CreatureSpawnEvent.SpawnReason.NATURAL,
                CreatureSpawnEvent.SpawnReason.CHUNK_GEN,
                CreatureSpawnEvent.SpawnReason.DEFAULT-> {
                    event.isCancelled = true
                } else -> {}
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity.world == PrivateMineHandler.getGridWorld()) {
            if (event.entity is Player) {
                event.isCancelled = true
            }
        }
    }

}
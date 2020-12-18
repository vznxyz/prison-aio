/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.listener

import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent

object PrivateMineWorldListeners : Listener {

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
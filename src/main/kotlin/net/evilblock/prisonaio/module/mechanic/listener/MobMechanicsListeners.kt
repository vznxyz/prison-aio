/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerReplenishTradeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

/**
 * Mob mechanics.
 */
object MobMechanicsListeners : Listener {

    /**
     * Prevents mobs from spawning for certain reasons.
     */
    @EventHandler(ignoreCancelled = true)
    fun onCreatureSpawnEventPreventSpawning(event: CreatureSpawnEvent) {
        when (event.spawnReason) {
            CreatureSpawnEvent.SpawnReason.DEFAULT,
            CreatureSpawnEvent.SpawnReason.NATURAL,
            CreatureSpawnEvent.SpawnReason.CHUNK_GEN,
            CreatureSpawnEvent.SpawnReason.ENDER_PEARL,
            CreatureSpawnEvent.SpawnReason.NETHER_PORTAL,
            CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK,
            CreatureSpawnEvent.SpawnReason.BUILD_WITHER,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            CreatureSpawnEvent.SpawnReason.LIGHTNING -> {
                event.isCancelled = true
            }
            else -> {
            }
        }
    }

    /**
     * Disables AI for all mobs.
     */
    @EventHandler(ignoreCancelled = true)
    fun onCreatureSpawnEventDisableAI(event: CreatureSpawnEvent) {
        event.entity.setAI(false)
    }

    /**
     * Removes villager trading.
     */
    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        if (event.rightClicked is Villager) {
            event.isCancelled = true
        }
    }

    /**
     * Removes villager trading.
     */
    @EventHandler(ignoreCancelled = true)
    fun onVillagerAcquireTradeEvent(event: VillagerAcquireTradeEvent) {
        event.isCancelled = true
    }

    /**
     * Removes villager trading.
     */
    @EventHandler(ignoreCancelled = true)
    fun onVillagerReplenishTradeEvent(event: VillagerReplenishTradeEvent) {
        event.isCancelled = true
    }

}
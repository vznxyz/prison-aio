/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.WorldSaveEvent

object GangWorldListeners : Listener {

    /**
     * Saves the grid whenever the world is saved.
     */
    @EventHandler
    fun onWorldSaveEvent(event: WorldSaveEvent) {
        if (event.world == GangHandler.getGridWorld()) {
            Tasks.async {
                GangHandler.saveGrid()
            }
        }
    }

    /**
     * Makes it only sunny weather in the grid world.
     */
    @EventHandler
    fun onWeatherChangeEvent(event: WeatherChangeEvent) {
        if (event.toWeatherState()) {
            event.isCancelled = true
        }

        event.world.thunderDuration = 0
        event.world.isThundering = false
        event.world.weatherDuration = 999999999
    }

    /**
     * Prevents natural creature spawns in the grid world.
     */
    @EventHandler
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        if (event.entity.world == GangHandler.getGridWorld()) {
            when (event.spawnReason) {
                CreatureSpawnEvent.SpawnReason.NATURAL,
                CreatureSpawnEvent.SpawnReason.CHUNK_GEN,
                CreatureSpawnEvent.SpawnReason.DEFAULT-> {
                    event.isCancelled = true
                } else -> {}
            }
        }
    }

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.player.world == GangHandler.getGridWorld() && event.to.y <= -20) {
            val visitingGang = GangHandler.getVisitingGang(event.player)
            if (visitingGang == null) {
                event.player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            } else {
                event.player.teleport(visitingGang.homeLocation)
            }
        }
    }

}
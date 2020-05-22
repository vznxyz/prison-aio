package net.evilblock.prisonaio.module.cell.listener

import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.WorldSaveEvent

object CellWorldListeners : Listener {

    /**
     * Saves the grid whenever the world is saved.
     */
    @EventHandler
    fun onWorldSaveEvent(event: WorldSaveEvent) {
        if (event.world == CellHandler.getGridWorld()) {
            CellHandler.saveGrid()
        }
    }

    /**
     * Makes it only sunny weather in the grid world.
     */
    @EventHandler
    fun onWeatherChangeEvent(event: WeatherChangeEvent) {
        event.world.thunderDuration = 0
        event.world.isThundering = false
        event.world.weatherDuration = 999999999
        event.isCancelled = true
    }

    /**
     * Prevents natural creature spawns in the grid world.
     */
    @EventHandler
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        if (event.entity.world == CellHandler.getGridWorld()) {
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
        if (event.player.world == CellHandler.getGridWorld() && event.to.y <= -20) {
            val visitingCell = CellHandler.getVisitingCell(event.player)
            if (visitingCell == null) {
                event.player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            } else {
                event.player.teleport(visitingCell.homeLocation)
            }
        }
    }

}
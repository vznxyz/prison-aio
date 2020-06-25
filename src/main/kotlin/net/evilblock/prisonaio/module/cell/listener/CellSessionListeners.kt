/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.listener

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object CellSessionListeners : Listener {

    /**
     * Updates the visiting cell cache for a player if they die in the grid world.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        if (event.entity.world == CellHandler.getGridWorld()) {
            CellHandler.getVisitingCell(event.entity)?.leaveSession(event.entity)
            CellHandler.updateVisitingCell(event.entity, null)

            event.entity.isFlying = false
            event.entity.allowFlight = false
        }
    }

    /**
     * Updates the visiting cell for a player when they login if they logged out while at a cell.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (event.player.world == CellHandler.getGridWorld()) {
            // query async
            PrisonAIO.instance.server.scheduler.runTaskAsynchronously(PrisonAIO.instance) {
                val previousCell = CellHandler.fetchPreviousCell(event.player.uniqueId)
                if (previousCell != null) {
                    // then join session sync
                    PrisonAIO.instance.server.scheduler.runTask(PrisonAIO.instance) {
                        CellHandler.attemptJoinSession(event.player, previousCell)
                    }
                } else {
                    PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                        event.player.teleport(Bukkit.getWorlds()[0].spawnLocation)
                    }, 6L)
                }
            }
        }
    }

    /**
     * Remove the player from the `visiting cell` cache when they logout.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val currentCell = CellHandler.getVisitingCell(event.player)
        currentCell?.leaveSession(event.player)

        CellHandler.updatePreviousCell(event.player.uniqueId, currentCell)
        CellHandler.updateVisitingCell(event.player, null)

        event.player.isFlying = false
        event.player.allowFlight = false
    }

    /**
     * Update the `visiting cell` cache when a player is teleported out of the grid world.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        val gridWorld = CellHandler.getGridWorld()

        if (event.from.world == gridWorld && event.to.world != gridWorld) {
            CellHandler.getVisitingCell(event.player)?.leaveSession(event.player)
            CellHandler.updateVisitingCell(event.player, null)

            event.player.isFlying = false

            if (event.player.gameMode != GameMode.CREATIVE) {
                event.player.allowFlight = false
            }
        }
    }

}
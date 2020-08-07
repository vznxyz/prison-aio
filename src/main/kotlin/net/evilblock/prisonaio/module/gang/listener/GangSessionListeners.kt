/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object GangSessionListeners : Listener {

    /**
     * Updates the visiting gang cache for a player if they die in the grid world.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        if (event.entity.world == GangHandler.getGridWorld()) {
            GangHandler.getVisitingGang(event.entity)?.leaveSession(event.entity)
            GangHandler.updateVisitingGang(event.entity, null)

            event.entity.isFlying = false
            event.entity.allowFlight = false
        }
    }

    /**
     * Updates the visiting gang for a player when they login if they logged out while at a gang.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (event.player.world == GangHandler.getGridWorld()) {
            // query async
            PrisonAIO.instance.server.scheduler.runTaskAsynchronously(PrisonAIO.instance) {
                val previousGang = GangHandler.fetchPreviousGang(event.player.uniqueId)
                if (previousGang != null) {
                    // then join session sync
                    PrisonAIO.instance.server.scheduler.runTask(PrisonAIO.instance) {
                        GangHandler.attemptJoinSession(event.player, previousGang)
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
     * Remove the player from the `visiting gang` cache when they logout.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val currentCell = GangHandler.getVisitingGang(event.player)
        currentCell?.leaveSession(event.player)

        GangHandler.updatePreviousGang(event.player.uniqueId, currentCell)
        GangHandler.updateVisitingGang(event.player, null)

        event.player.isFlying = false
        event.player.allowFlight = false
    }

    /**
     * Update the `visiting gang` cache when a player is teleported out of the grid world.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        val gridWorld = GangHandler.getGridWorld()

        if (event.from.world == gridWorld && event.to.world != gridWorld) {
            GangHandler.getVisitingGang(event.player)?.leaveSession(event.player)
            GangHandler.updateVisitingGang(event.player, null)

            event.player.isFlying = false

            if (event.player.gameMode != GameMode.CREATIVE) {
                event.player.allowFlight = false
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.listener

import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

/**
 * Primarily manages internal data such as the [PrivateMineHandler.currentlyAt] cache.
 */
object PrivateMineInternalDataListeners : Listener {

    /**
     * Updates the current mine for a player if they die in the grid world.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        if (event.entity.world == PrivateMineHandler.getGridWorld()) {
            PrivateMineHandler.updateCurrentMine(event.entity, null)
        }
    }

    /**
     * Updates the current mine for a player if they last logged out in a mine.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (event.player.world == PrivateMineHandler.getGridWorld()) {
            val previousMine = PrivateMineHandler.fetchPreviousMine(event.player.uniqueId)
            if (previousMine != null) {
                PrivateMineHandler.updateCurrentMine(event.player, previousMine)
            }
        }
    }

    /**
     * Remove the player from the current access cache when they logout.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        PrivateMineHandler.updateCurrentMine(event.player, null)
    }

    /**
     * Update the current access cache when a player is teleported out of the grid world.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        val gridWorld = PrivateMineHandler.getGridWorld()

        if (event.from.world == gridWorld && event.to.world != gridWorld) {
            PrivateMineHandler.updateCurrentMine(event.player, null)
        }
    }

}
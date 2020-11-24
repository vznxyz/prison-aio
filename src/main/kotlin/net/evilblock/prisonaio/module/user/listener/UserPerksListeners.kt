/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

object UserPerksListeners : Listener {

    /**
     * Restores preserved allow-flight and flying states when logging in.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (UserHandler.getUser(event.player.uniqueId).perks.isPerkEnabled(Perk.FLY)) {
            event.player.allowFlight = true
            event.player.isFlying = true
            event.player.updateInventory()
        }
    }

    /**
     * Preserves game-mode, allow-flight, and flying states when switching worlds.
     */
    @EventHandler
    fun onPlayerChangedWorldEvent(event: PlayerChangedWorldEvent) {
        val gameMode = event.player.gameMode
        val allowFlight = event.player.allowFlight
        val isFlying = event.player.isFlying

        Tasks.delayed(1L) {
            event.player.gameMode = gameMode
            event.player.allowFlight = allowFlight
            event.player.isFlying = isFlying
            event.player.updateInventory()
        }
    }

}
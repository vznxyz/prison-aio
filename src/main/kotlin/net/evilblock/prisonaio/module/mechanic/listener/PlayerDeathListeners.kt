/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

object PlayerDeathListeners : Listener {

    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        Tasks.delayed(6L) {
            event.player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            event.player.sendMessage("${ChatColor.YELLOW}Oh dear, you have died! You've respawned back at spawn.")
        }
    }

}
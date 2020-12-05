/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

object SpawnListeners : Listener {

    /**
     * Prevents placing boats for normal players.
     */
    @EventHandler(ignoreCancelled = true)
    fun onBoatPlace(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.inventory.itemInMainHand.type == Material.BOAT) {
                if (RegionBypass.hasBypass(event.player)) {
                    RegionBypass.attemptNotify(event.player)
                } else {
                    event.isCancelled = true
                }
            }
        }
    }

    /**
     * Prevents dropping items in spawn.
     */
    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (event.player.location.world == Bukkit.getWorlds()[0]) {
            if (RegionBypass.hasBypass(event.player)) {
                RegionBypass.attemptNotify(event.player)
                return
            }

            val spawnPoint = Bukkit.getWorlds()[0].spawnLocation
            val x = spawnPoint.x - 100.0 .. spawnPoint.x + 100.0
            val z = spawnPoint.z - 100.0 .. spawnPoint.z + 100.0

            if (event.player.location.x in x && event.player.location.z in z) {
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}You can't drop items within 100 blocks of the spawn point.")
            }
        }
    }

}
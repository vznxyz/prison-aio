/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.teleport.listener

import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object UserTeleportListeners : Listener {

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player

            val user = UserHandler.getUser(player)
            if (user.pendingTeleport != null) {
                val teleport = user.pendingTeleport!!
                try {
                    teleport.call.invoke(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                user.pendingTeleport = null

                player.sendMessage("${ChatColor.RED}Your ${teleport.name} ${ChatColor.RED}teleport has been cancelled because you took damage!")
            }
        }
    }

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        val user = UserHandler.getUser(event.player)
        if (user.pendingTeleport != null) {
            val teleport = user.pendingTeleport!!
            if (teleport.location.distance(event.player.location) > 2.0) {
                try {
                    teleport.call.invoke(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                user.pendingTeleport = null

                event.player.sendMessage("${ChatColor.RED}Your ${teleport.name} ${ChatColor.RED}teleport has been cancelled because you moved too far!")
            }
        }
    }

    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        val user = UserHandler.getUser(event.player)
        if (user.pendingTeleport != null) {
            val teleport = user.pendingTeleport!!
            if (teleport.location.world != event.player.world || teleport.location.distance(event.player.location) > 2.0) {
                try {
                    teleport.call.invoke(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                user.pendingTeleport = null

                event.player.sendMessage("${ChatColor.RED}Your ${teleport.name} ${ChatColor.RED}teleport has been cancelled because you moved too far!")
            }
        }
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        UserHandler.getUser(event.entity).pendingTeleport = null
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if (UserHandler.isUserLoaded(event.player)) {
            UserHandler.getUser(event.player).pendingTeleport = null
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.koth.listener

import net.evilblock.cubed.util.bukkit.EventUtils
import net.evilblock.prisonaio.module.koth.KOTH
import net.evilblock.prisonaio.module.koth.KOTHHandler
import net.evilblock.prisonaio.module.region.RegionHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object KOTHListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (!KOTHHandler.isActive()) {
            return
        }

        if (!EventUtils.hasPlayerMoved(event)) {
            return
        }

        val region = RegionHandler.findRegion(event.player)
        if (region is KOTH) {
            val koth = KOTHHandler.getActiveEvent()
            if (koth != null && koth.region == region) {
                koth.capturing.add(event.player)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (!KOTHHandler.isActive()) {
            return
        }

        val region = RegionHandler.findRegion(event.to)
        if (region is KOTH) {
            val koth = KOTHHandler.getActiveEvent()
            if (koth != null && koth.region == region) {
                koth.capturing.add(event.player)
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if (KOTHHandler.isActive()) {
            val koth = KOTHHandler.getActiveEvent()
            if (koth != null) {
                koth.capturing.remove(event.player)

                if (koth.capturer == event.player) {
                    koth.capturer = null
                }
            }
        }
    }

}
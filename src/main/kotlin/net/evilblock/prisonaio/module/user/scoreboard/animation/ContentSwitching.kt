/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.animation

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ContentSwitching : Runnable, Listener {

    private val nextSwitch: MutableMap<UUID, Pair<Boolean, Long>> = ConcurrentHashMap()

    override fun run() {
        for ((key, value) in nextSwitch) {
            if (System.currentTimeMillis() >= value.second) {
                nextSwitch[key] = Pair(!value.first, System.currentTimeMillis() + 7_000L)
            }
        }
    }

    @JvmStatic
    fun isPrimaryDisplay(player: Player): Boolean {
        if (!nextSwitch.containsKey(player.uniqueId)) {
            nextSwitch[player.uniqueId] = Pair(true, System.currentTimeMillis() + 7_000L)
        }
        return nextSwitch[player.uniqueId]!!.first
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        nextSwitch.remove(event.player.uniqueId)
    }

}
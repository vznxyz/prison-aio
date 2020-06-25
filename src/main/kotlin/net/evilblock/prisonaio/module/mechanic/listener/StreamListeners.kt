/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object StreamListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        event.joinMessage = null

        event.player.sendMessage("")
        event.player.sendMessage(" ${ChatColor.RED}${ChatColor.BOLD}Important Notice")
        event.player.sendMessage(" ${ChatColor.GRAY}Due to an issue that we can't mitigate until")
        event.player.sendMessage(" ${ChatColor.GRAY}the end of this map, we advise that you ${ChatColor.RED}${ChatColor.BOLD}DO NOT")
        event.player.sendMessage(" ${ChatColor.GRAY}change your username. Your user-data WILL be")
        event.player.sendMessage(" ${ChatColor.GRAY}reset and you ${ChatColor.RED}${ChatColor.BOLD}WILL NOT ${ChatColor.GRAY}be compensated.")
        event.player.sendMessage("")
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        event.quitMessage = null
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.listener

import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object PrematureLoadListeners : Listener {

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        if (!PrisonAIO.instance.loaded) {
            event.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
            event.kickMessage = "${ChatColor.RED}${ChatColor.BOLD}You can't join the server yet!"
        }
    }

}
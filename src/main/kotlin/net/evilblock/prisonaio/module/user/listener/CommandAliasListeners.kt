/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.module.user.UsersModule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandAliasListeners : Listener {

    @EventHandler
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        if (event.message.split(" ").size == 1) {
            for (alias in UsersModule.commandAliases) {
                if (event.message.startsWith("/${alias.first}", ignoreCase = true)) {
                    event.player.performCommand(alias.second)
                    event.isCancelled = true
                    return
                }
            }
        }
    }

}
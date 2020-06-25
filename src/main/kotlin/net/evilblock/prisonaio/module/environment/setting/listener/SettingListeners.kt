/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.environment.setting.listener

import net.evilblock.prisonaio.module.environment.analytic.Analytic
import net.evilblock.prisonaio.module.environment.setting.Setting
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object SettingListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (Setting.FIRST_JOIN_MESSAGE_TOGGLE.getValue()) {
            if (!event.player.hasPlayedBefore()) {
                val message = Setting.FIRST_JOIN_MESSAGE_FORMAT.getValue<String>()
                    .replace("{playerName}", event.player.name)
                    .replace("{playerDisplayName}", event.player.displayName)
                    .replace("{uniqueJoin}", Analytic.UNIQUE_JOINS.getValue<Int>().toString())

                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message)
                }
            }
        }
    }

}
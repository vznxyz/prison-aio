/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.admin.setting.listener

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.admin.analytic.Analytic
import net.evilblock.prisonaio.module.admin.setting.Setting
import net.evilblock.rift.bukkit.spoof.v1_12_R1.FakeEntityPlayer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object SettingListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (Setting.FIRST_JOIN_MESSAGE_TOGGLE.getValue()) {
            if (!event.player.hasPlayedBefore()) {
                if ((event.player as CraftPlayer).handle is FakeEntityPlayer) {
                    if (!Chance.random()) {
                        return
                    }
                }

                val message = Setting.FIRST_JOIN_MESSAGE_FORMAT.getValue<String>()
                    .replace("{playerName}", event.player.name)
                    .replace("{playerDisplayName}", event.player.displayName)
                    .replace("{uniqueJoin}", NumberUtils.format(Analytic.UNIQUE_JOINS.getValue<Int>()))

                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message)
                }
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar.path.listener

import net.evilblock.prisonaio.module.theme.impl.avatar.user.AvatarThemeUserData
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object AvatarPathListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (!event.player.hasPlayedBefore()) {
            val data = UserHandler.getUser(event.player.uniqueId).themeUserData as AvatarThemeUserData
            if (!data.hasBaseElement()) {

            }
        }
    }

}
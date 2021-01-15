/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.service

import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.Bukkit

object SlotExpirationService : Service {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!UserHandler.isUserLoaded(player)) {
                continue
            }

            val user = UserHandler.getUser(player)
            if (user.scoreboardSlots.isEmpty()) {
                continue
            }

            val iterator = user.scoreboardSlots.iterator()
            while (iterator.hasNext()) {
                val slot = iterator.next()
                if (slot.isExpired()) {
                    iterator.remove()
                }
            }
        }
    }

}
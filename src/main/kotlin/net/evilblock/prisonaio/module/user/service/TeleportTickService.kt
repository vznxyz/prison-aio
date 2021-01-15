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
import org.bukkit.entity.Player

object TeleportTickService : Service {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                runLogic(player)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun runLogic(player: Player) {
        val user = UserHandler.getUser(player)
        if (user.pendingTeleport != null) {
            val teleport = user.pendingTeleport!!
            if (teleport.isExpired()) {
                teleport.call.invoke(true)
                user.pendingTeleport = null
            }
        }
    }

}
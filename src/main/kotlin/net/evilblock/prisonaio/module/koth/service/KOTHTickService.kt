/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.koth.service

import net.evilblock.prisonaio.module.koth.KOTHHandler
import net.evilblock.prisonaio.service.Service

object KOTHTickService : Service {

    override fun run() {
        val event = KOTHHandler.getActiveEvent()
        if (event != null && event.active) {
            val iterator = event.capturing.iterator()
            while (iterator.hasNext()) {
                val player = iterator.next()
                if (!player.isOnline || !event.region.captureCuboid!!.contains(player)) {
                    iterator.remove()
                }
            }
        }
    }

}
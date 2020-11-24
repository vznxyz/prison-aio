/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.service

import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.service.Service

object GarbageCollectionService : Service {

    override fun run() {
        for (user in UserHandler.getUsers()) {
            try {
                user.grandExchangeData.expireNotifications()
                user.grandExchangeData.cleanupBidListings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
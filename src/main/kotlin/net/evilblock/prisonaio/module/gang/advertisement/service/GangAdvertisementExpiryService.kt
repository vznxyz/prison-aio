/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.advertisement.service

import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisement
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementHandler
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementType
import net.evilblock.prisonaio.service.Service

object GangAdvertisementExpiryService : Service {

    override fun run() {
        val expired = arrayListOf<GangAdvertisement>()

        for (advertisement in GangAdvertisementHandler.getAdvertisements()) {
            // check if gang still exists
            if (advertisement.type == GangAdvertisementType.GANG) {
                val gang = GangHandler.getGangById(advertisement.createdBy)
                if (gang == null) {
                    expired.add(advertisement)
                    continue
                }
            }

            if (advertisement.isExpired()) {
                expired.add(advertisement)
            }
        }

        for (advertisement in expired) {
            GangAdvertisementHandler.forgetAdvertisement(advertisement)
        }
    }

}
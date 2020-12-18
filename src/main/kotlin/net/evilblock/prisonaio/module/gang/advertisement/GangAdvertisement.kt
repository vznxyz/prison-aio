/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.advertisement

import java.util.*

class GangAdvertisement(
    val type: GangAdvertisementType,
    val createdBy: UUID,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= createdAt + GangAdvertisementHandler.EXPIRE_TIME
    }

    fun getRemainingTime(): Long {
        return (createdAt + GangAdvertisementHandler.EXPIRE_TIME) - System.currentTimeMillis()
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.bid

import net.evilblock.cubed.Cubed
import java.util.*

class AuctionBid(val createdBy: UUID) {

    val createdAt: Long = System.currentTimeMillis()

    fun getCreatorUsername(): String {
        return Cubed.instance.uuidCache.name(createdBy)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.listing.bid

import net.evilblock.cubed.Cubed
import java.math.BigInteger
import java.util.*

class GrandExchangeListingBid(val createdBy: UUID, val amount: BigInteger) {

    val id: UUID = UUID.randomUUID()
    val createdAt: Long = System.currentTimeMillis()

    fun getCreatorUsername(): String {
        return Cubed.instance.uuidCache.name(createdBy)
    }

}
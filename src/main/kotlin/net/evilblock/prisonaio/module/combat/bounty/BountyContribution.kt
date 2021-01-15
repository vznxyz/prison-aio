/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.bounty

import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import java.math.BigInteger
import java.util.*

data class BountyContribution(val value: BigInteger, val createdBy: UUID) {

    val createdAt: Long = System.currentTimeMillis()

    fun getCreatorUsername(): String {
        return Cubed.instance.uuidCache.name(createdBy)
    }

    fun getFormattedValue(currency: Currency.Type): String {
        return currency.format(value)
    }

}
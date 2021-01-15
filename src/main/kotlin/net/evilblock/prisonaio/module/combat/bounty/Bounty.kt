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
import java.util.concurrent.ConcurrentHashMap

class Bounty(
    val target: UUID,
    val createdBy: UUID,
    val currency: Currency.Type,
    var value: BigInteger
) {

    val createdAt: Long = System.currentTimeMillis()

    private val contributions: ConcurrentHashMap<UUID, BountyContribution> = ConcurrentHashMap()

    fun getTargetUsername(): String {
        return Cubed.instance.uuidCache.name(target)
    }

    fun getCreatorUsername(): String {
        return Cubed.instance.uuidCache.name(createdBy)
    }

    fun getFormattedValue(): String {
        return currency.format(value)
    }

    fun recalculateValue() {
        var sum = BigInteger("0")
        for (contribution in contributions) {
            sum += contribution.value.value
        }
        value = sum
    }

    fun getContributions(): Collection<BountyContribution> {
        return contributions.values
    }

    fun isContributor(player: UUID): Boolean {
        return contributions.containsKey(player)
    }

    fun getContribution(player: UUID): BountyContribution? {
        return contributions[player]
    }

    fun trackContribution(contribution: BountyContribution) {
        contributions[contribution.createdBy] = contribution
    }

}
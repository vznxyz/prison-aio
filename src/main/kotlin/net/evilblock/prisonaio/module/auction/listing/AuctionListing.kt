/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.listing

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.Duration
import net.evilblock.prisonaio.module.auction.bid.AuctionBid
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.inventory.ItemStack
import java.util.*

class AuctionListing(
    val id: UUID,
    private val goods: ItemStack,
    val duration: Duration,
    val createdBy: UUID
) {

    val createdAt: Long = System.currentTimeMillis()

    var finished: Boolean = false
    var finishedAt: Long? = null

    private var currencyType: Currency = Currency.Money
    private var currencyAmount: Number = 0

    private val bids: LinkedList<AuctionBid> = LinkedList()

    fun getGoods(): ItemStack {
        return goods.clone()
    }

    fun getCreatorUsername(): String {
        return Cubed.instance.uuidCache.name(createdBy)
    }

    fun getLatestBid(): AuctionBid? {
        return bids.minBy { it.createdAt }
    }

    fun isFinished(): Boolean {
        return finished
    }

    fun getCurrencyType(): Currency {
        return currencyType
    }

    fun getCurrencyAmount(): Number {
        return currencyAmount
    }

    fun updatePrice(currency: Currency, amount: Number) {
        currencyType = currency
        currencyAmount = amount
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.statistic

import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.event.AsyncPlayTimeSyncEvent
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round

class UserStatistics(@Transient internal var user: User) {

    private var playTime: Long = 0L
    @Transient
    internal var lastPlayTimeSync: Long = 0L

    private var kills: Int = 0
    private var deaths: Int = 0

    private var blocksMined: Int = 0
    private val blocksMinedAtMines: MutableMap<String, Int> = ConcurrentHashMap()

    private var coinflipWins: Int = 0
    private var coinflipLosses: Int = 0
    private var coinflipProfit: MutableMap<Currency.Type, BigInteger> = EnumMap(Currency.Type::class.java)

    private var tradesCompleted: Int = 0

    fun getBlocksMined(): Int {
        return blocksMined
    }

    fun setBlocksMined(amount: Int) {
        blocksMined = amount
        user.requiresSave = true
    }

    fun addBlocksMined(amount: Int) {
        blocksMined += amount
        user.requiresSave = true
    }

    fun getBlocksMinedAtMine(mine: Mine): Int {
        return blocksMinedAtMines.getOrDefault(mine.id.toLowerCase(), 0)
    }

    fun setBlocksMinedAtMine(mine: Mine, amount: Int) {
        blocksMinedAtMines[mine.id.toLowerCase()] = amount
        user.requiresSave = true
    }

    fun addBlocksMinedAtMine(mine: Mine, amount: Int) {
        val previous = blocksMinedAtMines.getOrDefault(mine.id.toLowerCase(), 0)
        blocksMinedAtMines[mine.id.toLowerCase()] = previous + amount
        user.requiresSave = true
    }

    fun syncPlayTime() {
        if (user.cacheExpiry != null) {
            return
        }

        val offset = getLivePlayTime() - playTime
        AsyncPlayTimeSyncEvent(user = user, offset = offset).call()

        playTime = getLivePlayTime()
        lastPlayTimeSync = System.currentTimeMillis()
        user.requiresSave = true
    }

    fun getPlayTime(): Long {
        return playTime
    }

    fun getLivePlayTime(): Long {
        return if (lastPlayTimeSync == 0L) {
            playTime
        } else {
            playTime + (System.currentTimeMillis() - lastPlayTimeSync)
        }
    }

    fun setPlayTime(time: Long) {
        playTime = time
        user.requiresSave = true
    }

    fun getKills(): Int {
        return kills
    }

    fun addKill() {
        kills++
        user.requiresSave = true
    }

    fun getDeaths(): Int {
        return deaths
    }

    fun addDeath() {
        deaths++
        user.requiresSave = true
    }

    fun getCoinflipWins(): Int {
        return coinflipWins
    }

    fun addCoinflipWin() {
        coinflipWins++
        user.requiresSave = true
    }

    fun getCoinflipLosses(): Int {
        return coinflipLosses
    }

    fun addCoinflipLoss() {
        coinflipLosses++
        user.requiresSave = true
    }

    fun getCoinflipProfit(currency: Currency.Type): BigInteger {
        if (!coinflipProfit.containsKey(currency)) {
            coinflipProfit[currency] = BigInteger("0")
        }
        return coinflipProfit[currency]!!
    }

    fun addCoinflipProfit(currency: Currency.Type, amount: Number) {
        val add = when (amount) {
            is Double -> {
                BigDecimal(round(amount)).toBigIntegerExact()
            }
            is BigDecimal -> {
                amount.toBigInteger()
            }
            else -> {
                BigInteger(amount.toString())
            }
        }

        coinflipProfit[currency] = getCoinflipProfit(currency) + add
        user.requiresSave = true
    }

    fun subtractCoinflipProfit(currency: Currency.Type, amount: Number) {
        val subtract = when (amount) {
            is Double -> {
                BigDecimal(amount.toString()).toBigIntegerExact()
            }
            is BigDecimal -> {
                amount.toBigInteger()
            }
            else -> {
                BigInteger(amount.toString())
            }
        }

        coinflipProfit[currency] = getCoinflipProfit(currency) - subtract
        user.requiresSave = true
    }

    fun getTradesCompleted(): Int {
        return tradesCompleted
    }

    fun addTradeCompleted() {
        tradesCompleted++
        user.requiresSave = true
    }

}
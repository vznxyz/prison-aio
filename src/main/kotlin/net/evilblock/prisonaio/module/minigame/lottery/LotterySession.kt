/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.lottery

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class LotterySession(private val currency: Currency.Type) {

    private var currentPot: BigInteger = LotteryHandler.getStartingPot(currency)
    private val ownedTickets: MutableMap<UUID, Int> = ConcurrentHashMap()
    private val expiresAt: Long = System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1L)

    fun getCurrencyType(): Currency.Type {
        return currency
    }

    fun getCurrentPot(): BigInteger {
        return currentPot
    }

    fun getOwnedTickets(player: Player): Int {
        return ownedTickets.getOrDefault(player.uniqueId, 0)
    }

    fun getOwnedTickets(uuid: UUID): Int {
        return ownedTickets.getOrDefault(uuid, 0)
    }

    fun purchaseTickets(player: Player, tickets: Int): Boolean {
        val unitPrice = LotteryHandler.getTicketPrice()
        val totalCost = BigInteger.valueOf(tickets.toLong()) * unitPrice

        if (!currency.has(player.uniqueId, totalCost)) {
            player.sendMessage("${ChatColor.RED}You don't have enough ${currency.getName()} to purchase ${NumberUtils.format(tickets)}${ChatColor.RED}!")
            return false
        }



        return true
    }

}
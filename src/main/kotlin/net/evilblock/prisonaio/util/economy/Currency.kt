/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.economy

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

abstract class Currency<T>(internal val amount: T) {

    fun get(): T {
        return amount
    }

    fun int(): Int {
        return amount as Int
    }

    fun double(): Double {
        return amount as Double
    }

    fun long(): Long {
        return amount as Long
    }

    abstract fun isMoney(): Boolean

    abstract fun isTokens(): Boolean

    abstract fun has(player: OfflinePlayer): Boolean

    abstract fun give(player: OfflinePlayer)

    abstract fun take(player: OfflinePlayer)

    class Money(amount: Double) : Currency<Double>(amount) {
        override fun isMoney(): Boolean {
            return true
        }

        override fun isTokens(): Boolean {
            return false
        }

        override fun has(player: OfflinePlayer): Boolean {
            return VaultHook.useEconomyAndReturn { economy -> economy.has(player, amount) }
        }

        override fun give(player: OfflinePlayer) {
            VaultHook.useEconomy { economy -> economy.depositPlayer(player, amount) }
        }

        override fun take(player: OfflinePlayer) {
            VaultHook.useEconomy { economy -> economy.withdrawPlayer(player, amount) }
        }
    }

    class Tokens(amount: Long) : Currency<Long>(amount) {
        override fun isMoney(): Boolean {
            return false
        }

        override fun isTokens(): Boolean {
            return true
        }

        override fun has(player: OfflinePlayer): Boolean {
            val user = UserHandler.getUser(player.uniqueId)
            return user.getTokensBalance() >= amount
        }

        override fun give(player: OfflinePlayer) {
            val user = if (UserHandler.isUserLoaded(player.uniqueId)) {
                UserHandler.getUser(player.uniqueId)
            } else {
                if (Bukkit.isPrimaryThread()) {
                    throw IllegalStateException("Couldn't give token currency to offline user on primary thread (requires load)")
                }

                UserHandler.fetchUser(player.uniqueId)
            }

            user.addTokensBalance(amount)
        }

        override fun take(player: OfflinePlayer) {
            UserHandler.getUser(player.uniqueId).subtractTokensBalance(amount)
        }

    }

}
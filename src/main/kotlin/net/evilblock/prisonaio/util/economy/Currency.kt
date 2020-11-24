/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.economy

import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.*

interface Currency {

    fun getName(): String

    fun getAmountContext(): String

    fun get(player: UUID): Number

    fun has(player: UUID, amount: Number): Boolean

    fun give(player: UUID, amount: Number)

    fun take(player: UUID, amount: Number)

    fun format(amount: Number): String

    fun toType(): Type {
        return when (this) {
            is Type -> {
                this
            }
            is Money -> {
                Type.MONEY
            }
            is Tokens -> {
                Type.TOKENS
            }
            is PrestigeTokens -> {
                Type.PRESTIGE_TOKENS
            }
            else -> {
                throw IllegalStateException("Unsupported currency: ${this::class.java.name}")
            }
        }
    }

    object Money : Currency {
        override fun getName(): String {
            return "money"
        }

        override fun getAmountContext(): String {
            return "much"
        }

        override fun get(player: UUID): Number {
            return UserHandler.getOrLoadAndCacheUser(player).getMoneyBalance()
        }

        override fun has(player: UUID, amount: Number): Boolean {
            return UserHandler.getOrLoadAndCacheUser(player).hasMoneyBalance(amount.toDouble())
        }

        override fun give(player: UUID, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player).addMoneyBalance(amount.toDouble())
        }

        override fun take(player: UUID, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player).subtractMoneyBalance(amount.toDouble())
        }

        override fun format(amount: Number): String {
            return Formats.formatMoney(amount.toDouble())
        }
    }

    object Tokens : Currency {
        override fun getName(): String {
            return "tokens"
        }

        override fun getAmountContext(): String {
            return "many"
        }

        override fun get(player: UUID): Number {
            return UserHandler.getOrLoadAndCacheUser(player).getTokenBalance()
        }

        override fun has(player: UUID, amount: Number): Boolean {
            return UserHandler.getOrLoadAndCacheUser(player).hasTokenBalance(amount.toLong())
        }

        override fun give(player: UUID, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player).addTokensBalance(amount.toLong())
        }

        override fun take(player: UUID, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player).subtractTokensBalance(amount.toLong())
        }

        override fun format(amount: Number): String {
            return Formats.formatTokens(amount.toLong())
        }
    }

    object PrestigeTokens : Currency {
        override fun getName(): String {
            return "prestige tokens"
        }

        override fun getAmountContext(): String {
            return "many"
        }

        override fun get(player: UUID): Number {
            return UserHandler.getOrLoadAndCacheUser(player).getPrestigeTokens()
        }

        override fun has(player: UUID, amount: Number): Boolean {
            return UserHandler.getOrLoadAndCacheUser(player).hasPrestigeTokens(amount.toInt())
        }

        override fun give(player: UUID, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player).addPrestigeTokens(amount.toInt())
        }

        override fun take(player: UUID, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player).subtractPrestigeTokens(amount.toInt())
        }

        override fun format(amount: Number): String {
            return Formats.formatPrestigeTokens(amount.toInt())
        }
    }

    enum class Type(private val currency: Currency, val displayName: String, val icon: Material) : Currency {
        MONEY(Money, "${ChatColor.GREEN}${ChatColor.BOLD}Money", Material.DOUBLE_PLANT),
        TOKENS(Tokens, "${ChatColor.GOLD}${ChatColor.BOLD}Tokens", Material.MAGMA_CREAM),
        PRESTIGE_TOKENS(PrestigeTokens, "${ChatColor.RED}${ChatColor.BOLD}Prestige Tokens", Material.FIREBALL);

        override fun getName(): String {
            return currency.getName()
        }

        override fun getAmountContext(): String {
            return currency.getAmountContext()
        }

        override fun get(player: UUID): Number {
            return currency.get(player)
        }

        override fun has(player: UUID, amount: Number): Boolean {
            return currency.has(player, amount)
        }

        override fun give(player: UUID, amount: Number) {
            currency.give(player, amount)
        }

        override fun take(player: UUID, amount: Number) {
            currency.take(player, amount)
        }

        override fun format(amount: Number): String {
            return currency.format(amount)
        }
    }

}
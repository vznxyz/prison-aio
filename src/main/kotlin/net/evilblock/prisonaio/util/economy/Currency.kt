/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.economy

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer

interface Currency {

    fun getName(): String

    fun get(player: OfflinePlayer): Number

    fun has(player: OfflinePlayer, amount: Number): Boolean

    fun give(player: OfflinePlayer, amount: Number)

    fun take(player: OfflinePlayer, amount: Number)

    fun format(amount: Number): String

    fun toType(): Type {
        return when (this) {
            is Currency.Type -> {
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

        override fun get(player: OfflinePlayer): Number {
            return VaultHook.useEconomyAndReturn { economy -> economy.getBalance(player) }
        }

        override fun has(player: OfflinePlayer, amount: Number): Boolean {
            return VaultHook.useEconomyAndReturn { economy -> economy.has(player, amount.toDouble()) }
        }

        override fun give(player: OfflinePlayer, amount: Number) {
            VaultHook.useEconomy { economy -> economy.depositPlayer(player, amount.toDouble()) }
        }

        override fun take(player: OfflinePlayer, amount: Number) {
            VaultHook.useEconomy { economy -> economy.withdrawPlayer(player, amount.toDouble()) }
        }

        override fun format(amount: Number): String {
            return Formats.formatMoney(amount.toDouble())
        }
    }

    object Tokens : Currency {
        override fun getName(): String {
            return "tokens"
        }

        override fun get(player: OfflinePlayer): Number {
            return UserHandler.getOrLoadAndCacheUser(player.uniqueId).getTokenBalance()
        }

        override fun has(player: OfflinePlayer, amount: Number): Boolean {
            return UserHandler.getOrLoadAndCacheUser(player.uniqueId).getTokenBalance() >= amount.toLong()
        }

        override fun give(player: OfflinePlayer, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player.uniqueId).addTokensBalance(amount.toLong())
        }

        override fun take(player: OfflinePlayer, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player.uniqueId).subtractTokensBalance(amount.toLong())
        }

        override fun format(amount: Number): String {
            return Formats.formatTokens(amount.toLong())
        }
    }

    object PrestigeTokens : Currency {
        override fun getName(): String {
            return "prestige tokens"
        }

        override fun get(player: OfflinePlayer): Number {
            return UserHandler.getOrLoadAndCacheUser(player.uniqueId).getPrestigeTokens()
        }

        override fun has(player: OfflinePlayer, amount: Number): Boolean {
            return UserHandler.getOrLoadAndCacheUser(player.uniqueId).getPrestigeTokens() >= amount.toInt()
        }

        override fun give(player: OfflinePlayer, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player.uniqueId).addPrestigeTokens(amount.toInt())
        }

        override fun take(player: OfflinePlayer, amount: Number) {
            UserHandler.getOrLoadAndCacheUser(player.uniqueId).subtractPrestigeTokens(amount.toInt())
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

        override fun get(player: OfflinePlayer): Number {
            return currency.get(player)
        }

        override fun has(player: OfflinePlayer, amount: Number): Boolean {
            return currency.has(player, amount)
        }

        override fun give(player: OfflinePlayer, amount: Number) {
            currency.give(player, amount)
        }

        override fun take(player: OfflinePlayer, amount: Number) {
            currency.take(player, amount)
        }

        override fun format(amount: Number): String {
            return currency.format(amount)
        }
    }

}
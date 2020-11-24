/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.math.BigInteger
import java.text.NumberFormat
import java.util.*

object Formats {

    @JvmStatic
    fun formatPlayer(uuid: UUID, prefix: String = "${ChatColor.RESET}"): String {
        return "$prefix${Cubed.instance.uuidCache.name(uuid)}"
    }

    @JvmStatic
    fun formatPlayer(player: Player, prefix: String = "${ChatColor.RESET}"): String {
        return if (player.hasMetadata("EP_PLAYER_LIST_NAME")) {
            ChatColor.translateAlternateColorCodes('&', prefix + player.getMetadata(MessagingManager.getMetadataAdapterKey())[0].asString() + player.displayName)
        } else {
            "${ChatColor.translateAlternateColorCodes('&', prefix)}${player.name}"
        }
    }

    @JvmStatic
    fun capitalizeFully(name: String): String {
        return if (name.length > 1) {
            if (name.contains("_")) {
                val sbName = StringBuilder()

                for (subName in name.split("_").toTypedArray()) {
                    sbName.append(subName.substring(0, 1).toUpperCase() + subName.substring(1).toLowerCase()).append(" ")
                }

                sbName.toString().substring(0, sbName.length - 1)
            } else {
                name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase()
            }
        } else {
            name.toUpperCase()
        }
    }

    @JvmStatic
    fun formatItemStack(itemStack: ItemStack): String {
        if (itemStack.hasItemMeta() && itemStack.itemMeta.hasDisplayName()) {
            return itemStack.itemMeta.displayName
        }
        return capitalizeFully(itemStack.type.name)
    }

    @JvmStatic
    fun formatMoney(amount: Double): String {
        val formatted = NumberFormat.getInstance().format(amount)
        return "${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}$formatted"
    }

    @JvmStatic
    fun formatMoney(amount: BigDecimal): String {
        if (amount.toDouble() < Long.MAX_VALUE) {
            return formatMoney(amount.toDouble())
        }

        return "${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}${String.format("%,e", amount)}"
    }

    @JvmStatic
    fun formatTokens(amount: Long): String {
        val formatted = NumberFormat.getInstance().format(amount)
        return "${ChatColor.RED}${Constants.TOKENS_SYMBOL}${ChatColor.YELLOW}${ChatColor.BOLD}$formatted"
    }

    @JvmStatic
    fun formatTokens(amount: BigInteger): String {
        if (amount.toLong() < Long.MAX_VALUE) {
            return formatTokens(amount.toLong())
        }

        return "${ChatColor.RED}${Constants.TOKENS_SYMBOL}${ChatColor.YELLOW}${ChatColor.BOLD}${String.format("%,d", amount)}"
    }

    @JvmStatic
    fun formatPrestigeTokens(amount: Int): String {
        val formatted = NumberFormat.getInstance().format(amount)
        return "${ChatColor.AQUA}${ChatColor.BOLD}$formatted ${TextUtil.pluralize(amount, "Prestige Token", "Prestige Tokens")}"
    }

}
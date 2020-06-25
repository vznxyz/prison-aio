/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.bank

import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.util.*

data class BankNote(val uuid: UUID = UUID.randomUUID(),
                    val value: Double,
                    val useTokens: Boolean,
                    var issuedTo: UUID,
                    var issuedAt: Date = Date.from(Instant.now()),
                    var issuedBy: UUID? = null,
                    var reason: String = "",
                    var redeemed: Boolean = false,
                    var dupedUseAttempts: Int = 0) {

    fun toItemStack(): ItemStack {
        return ItemBuilder
            .of(Material.PAPER)
            .name("${getFormattedValue()} ${ChatColor.GREEN}${ChatColor.BOLD}Bank Note")
            .setLore(listOf(
                HiddenLore.encodeString(uuid.toString()),
                "${ChatColor.GRAY}Right-click this bank note to deposit",
                "${ChatColor.GRAY}its value into your account."
            ))
            .build()
    }

    fun getPlainFormat(): String {
        return if (useTokens) {
            "${value.toLong()} tokens"
        } else {
            "$${value}"
        }
    }

    fun getFormattedValue(): String {
        return if (useTokens) {
            Formats.formatTokens(value.toLong())
        } else {
            Formats.formatMoney(value)
        }
    }

    fun redeem(player: Player) {
        redeemed = true

        if (useTokens) {
            val user = UserHandler.getUser(player.uniqueId)
            user.addTokensBalance(value.toLong())
        } else {
            VaultHook.useEconomy { economy -> economy.depositPlayer(player, value) }
        }

        player.sendMessage("${ChatColor.GREEN}You redeemed ${getFormattedValue()} ${ChatColor.GREEN}into your account.")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.receipt

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.NumberFormat
import java.util.*

data class ShopReceipt(
    val result: TransactionResult,
    val uuid: UUID = UUID.randomUUID(),
    val shop: Shop,
    val receiptType: ShopReceiptType,
    val items: List<ShopReceiptItem> = emptyList(),
    val multiplier: Double = 1.0,
    val finalCost: Double = 0.0
) {

    val createdAt: Long = System.currentTimeMillis()

    fun sendCompact(player: Player, firstOfChain: Boolean = true) {
        if (firstOfChain) {
            player.sendMessage("")
        }

        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Items ${receiptType.displayName} ${receiptType.context} ${shop.name}")

        val formattedItemsSold = NumberFormat.getInstance().format(items.sumBy { it.item.amount })

        val details = StringBuilder(" ${ChatColor.GRAY}You ${receiptType.displayName.toLowerCase()} ${ChatColor.GREEN}${ChatColor.BOLD}$formattedItemsSold ${ChatColor.GRAY}items for a total of ${Formats.formatMoney(finalCost)}")

        if (multiplier != 1.0) {
            details.append(" ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}$multiplier MULTI${ChatColor.GRAY}).")
        } else {
            details.append("${ChatColor.GRAY}.")
        }

        player.sendMessage(details.toString())

        FancyMessage(" ")
            .then("${ChatColor.GRAY}[${ChatColor.YELLOW}${ChatColor.BOLD}VIEW RECEIPT${ChatColor.GRAY}]")
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to view your receipt."))
            .command("/receipt $uuid")
            .send(player)

        player.sendMessage("")
    }

    fun sendDetailed(player: Player) {
        player.sendMessage("")
        player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}Listing receipt items... ${ChatColor.GRAY}(${shop.name} Shop${ChatColor.GRAY})")
        player.sendMessage("")

        val transactionContext = if (receiptType == ShopReceiptType.SELL) {
            "${ChatColor.GREEN}${ChatColor.BOLD}SOLD FOR"
        } else {
            "${ChatColor.RED}${ChatColor.BOLD}BOUGHT FOR"
        }

        for (receiptItem in items) {
            val msgBuilder = StringBuilder().append(" ${ChatColor.GRAY}- x${receiptItem.item.amount} ${ItemUtils.getName(receiptItem.item)} $transactionContext ${ChatColor.AQUA}\$${ChatColor.GREEN}${NumberUtils.format(receiptItem.getSellCost())}")

            if (receiptItem.multiplier != 1.0) {
                msgBuilder.append(" ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${receiptItem.multiplier} MULTI${ChatColor.GRAY})")
            }

            player.sendMessage(msgBuilder.toString())
        }

        player.sendMessage("")

        if (multiplier != 1.0) {
            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}Total: ${ChatColor.AQUA}$${ChatColor.GREEN}${NumberUtils.format(finalCost)} ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${multiplier} MULTI${ChatColor.GRAY})")
        } else {
            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}Total: ${ChatColor.AQUA}$${ChatColor.GREEN}${NumberUtils.format(finalCost)}")
        }

        player.sendMessage("")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object ShopReceiptParameterType : ParameterType<ShopReceipt?> {

    override fun transform(sender: CommandSender, source: String): ShopReceipt? {
        try {
            val receipt = ShopHandler.getReceiptById(sender as Player, UUID.fromString(source))
            if (receipt != null) {
                return receipt
            }
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Invalid Receipt ID.")
        }

        sender.sendMessage("${ChatColor.RED}Couldn't find receipt by that ID. Receipts expire after 30 seconds.")
        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList()
    }

}
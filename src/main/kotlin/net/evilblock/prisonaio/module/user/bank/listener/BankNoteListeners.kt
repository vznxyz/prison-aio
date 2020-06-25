/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.bank.listener

import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import net.evilblock.prisonaio.util.Constants
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object BankNoteListeners : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!(event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR)) {
            return
        }

        val itemInHand = event.player.inventory.itemInMainHand ?: return

        if (BankNoteHandler.isBankNoteItemStack(itemInHand)) {
            event.isCancelled = true

            val bankNote = BankNoteHandler.findBankNote(BankNoteHandler.extractId(itemInHand)) ?: return
            if (bankNote.redeemed) {
                event.player.sendMessage("${ChatColor.RED}The bank note you're trying to redeem seems to be duplicated. Please discard the item. If you believe this is an error, please contact the support team.")
                bankNote.dupedUseAttempts++

                val log = StringBuilder()
                    .append("${event.player.name} (${event.player.uniqueId})")
                    .append(" tried to redeem a bank-note worth ")
                    .append(bankNote.getPlainFormat())
                    .append(" but failed because of anti-dupe")

                BankNoteHandler.logFile.commit(log.toString())
                return
            }

            val announce = if (bankNote.useTokens) {
                bankNote.value >= 1_000_000L
            } else {
                bankNote.value >= 50_000_000_000_000
            }

            if (announce) {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission(Permissions.BANK_NOTES_ADMIN)) {
                        player.sendMessage("${Constants.ADMIN_PREFIX}${event.player.name} redeemed a bank-note worth ${bankNote.getFormattedValue()}${ChatColor.GRAY}.")
                    }
                }
            }

            val log = StringBuilder()
                .append("${event.player.name} (${event.player.uniqueId}, ${event.player.address.address.hostAddress})")
                .append(" redeemed a bank-note worth ")
                .append(bankNote.getPlainFormat())

            BankNoteHandler.logFile.commit(log.toString())

            bankNote.redeem(event.player)

            if (itemInHand.amount <= 1) {
                event.player.inventory.itemInMainHand = ItemStack(Material.AIR)
            } else {
                itemInHand.amount = itemInHand.amount - 1
            }

            event.player.updateInventory()
        }
    }

}
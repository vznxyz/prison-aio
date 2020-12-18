/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.bank.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.bank.BankNote
import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.util.*

object BankNoteGiveCommand {

    @Command(
        names = ["bn give", "banknote give"],
        description = "Give a bank note to a player",
        permission = "prisonaio.banknotes.give"
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") target: Player,
        @Param(name = "amount") moneyValue: Double,
        @Param(name = "currency") currency: Currency.Type,
        @Param(name = "reason", defaultValue = "Unspecified", wildcard = true) reason: String
    ) {
        val issuedBy: UUID? = if (sender is Player) {
            sender.uniqueId
        } else {
            null
        }

        val bankNote = BankNote(
            value = BigDecimal(moneyValue),
            currency = currency,
            issuedTo = target.uniqueId,
            issuedBy = issuedBy,
            reason = reason
        )

        BankNoteHandler.trackBankNote(bankNote)

        val formattedValue = bankNote.getFormattedValue()

        if (target.inventory.firstEmpty() == -1) {
            target.enderChest.addItem(bankNote.toItemStack())
        } else {
            target.inventory.addItem(bankNote.toItemStack())
            target.updateInventory()
        }

        sender.sendMessage("${ChatColor.GREEN}You've given a $formattedValue ${ChatColor.GREEN}bank note to ${ChatColor.YELLOW}${target.name}${ChatColor.GREEN}.")

        if (sender is Player) {
            target.sendMessage("${ChatColor.GREEN}You were given a $formattedValue ${ChatColor.GREEN}bank note by ${ChatColor.YELLOW}${sender.name}${ChatColor.GREEN}.")
        } else {
            target.sendMessage("${ChatColor.GREEN}You were given a $formattedValue ${ChatColor.GREEN}bank note.")
        }
    }

}
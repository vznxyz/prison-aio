/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.bank.BankNote
import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.math.BigDecimal

object TokensWithdrawCommand {

    @Command(
        names = ["token withdraw", "tokens withdraw", "bn withdraw tokens", "banknote withdraw tokens"],
        description = "Withdraw tokens from your account to physical bank notes"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "amount") tokens: Long) {
        if (tokens < 1) {
            player.sendMessage("${ChatColor.RED}You must withdraw at least 1 token.")
            return
        }

        val user = UserHandler.getUser(player.uniqueId)

        if (!user.hasTokenBalance(tokens)){
            player.sendMessage("${ChatColor.RED}You don't have enough tokens in your account to withdraw that amount.")
            return
        }

        if (player.inventory.firstEmpty() == -1) {
            player.sendMessage("${ChatColor.RED}You need at least one empty inventory slot to withdraw to a bank note.")
            return
        }

        user.subtractTokensBalance(tokens)
        user.requiresSave()

        val bankNote = BankNote(
            value = BigDecimal(tokens.toDouble()),
            currency = Currency.Type.TOKENS,
            issuedTo = player.uniqueId,
            reason = "Manual Withdraw"
        )

        BankNoteHandler.trackBankNote(bankNote)

        player.inventory.addItem(bankNote.toItemStack())
        player.updateInventory()

        player.sendMessage("${ChatColor.GREEN}You've withdrawn ${bankNote.getFormattedValue()} ${ChatColor.GREEN}from your account to a bank note.")
    }

}
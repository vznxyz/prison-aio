/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.user.bank.BankNote
import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object WithdrawCommand {

    @Command(
        names = ["withdraw", "bn withdraw money", "banknote withdraw money"],
        description = "Withdraw money from your account to physical bank notes"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "amount") money: Double) {
        if (money < 1) {
            player.sendMessage("${ChatColor.RED}You must withdraw at least $1.")
            return
        }

        val balance = VaultHook.getBalance(player.uniqueId)

        if (money > balance) {
            player.sendMessage("${ChatColor.RED}You don't have enough money in your account to withdraw that amount.")
            return
        }

        if (player.inventory.firstEmpty() == -1) {
            player.sendMessage("${ChatColor.RED}You need at least one empty inventory slot to withdraw to a bank note.")
            return
        }

        VaultHook.useEconomy { economy -> economy.withdrawPlayer(player, money) }

        val bankNote = BankNote(
            value = money,
            useTokens = false,
            issuedTo = player.uniqueId,
            reason = "Manual Withdraw"
        )

        BankNoteHandler.trackBankNote(bankNote)

        player.inventory.addItem(bankNote.toItemStack())
        player.updateInventory()

        player.sendMessage("${ChatColor.GREEN}You've withdrawn ${bankNote.getFormattedValue()} ${ChatColor.GREEN}from your account to a bank note.")
    }

}
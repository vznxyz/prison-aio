/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bson.Document
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal

object EconomyResetAllCommand {

    @Command(
        names = ["economy reset-all", "eco reset-all"],
        description = "Reset all players' money balances",
        permission = Permissions.ECONOMY_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender !is ConsoleCommandSender) {
            sender.sendMessage("${ChatColor.RED}This command must be executed through console.")
            return
        }

        val result = UserHandler.getCollection().updateMany(Document(), Document("\$set", Document("moneyBalance", "0")))

        for (user in UserHandler.getUsers()) {
            user.updateMoneyBalance(BigDecimal(0.0))
        }

        sender.sendMessage("${ChatColor.GOLD}Matched ${result.matchedCount} documents, modified ${result.modifiedCount} documents!")

        val log = StringBuilder().append(sender.name)

        if (sender is Player) {
            log.append(" (${sender.uniqueId}, ${sender.address.address.hostAddress})")
        }

        log.append(" reset all player's balances")

        UserHandler.economyLogFile.commit(log.toString())
    }

}
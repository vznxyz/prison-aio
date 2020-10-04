/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal

object EconomySetCommand {

    @Command(
        names = ["economy set", "eco set"],
        description = "Reset a player's money balance",
        permission = Permissions.ECONOMY_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") user: User, @Param(name = "amount") amount: Double) {
        user.updateMoneyBalance(BigDecimal(amount))
        sender.sendMessage("${ChatColor.GOLD}Set ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GOLD}'s balance to ${Formats.formatMoney(user.getMoneyBalance())}${ChatColor.GOLD}!")

        val log = StringBuilder().append(sender.name)

        if (sender is Player) {
            log.append(" (${sender.uniqueId}, ${sender.address.address.hostAddress})")
        }

        log.append(" set ${user.getUsername()}'s (${user.uuid}) balance to $amount")

        UserHandler.economyLogFile.commit(log.toString())
    }

}
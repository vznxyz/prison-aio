package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.text.NumberFormat

object TokensGiveCommand {

    @Command(
        names = ["token give", "tokens give"],
        description = "Adds tokens to a player's balance",
        permission = "prisonaio.tokens.give",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "amount") amount: Long) {
        user.addTokensBalance(amount)

        val formattedAmount = NumberFormat.getInstance().format(amount)
        sender.sendMessage("${ChatColor.GREEN}You added ${ChatColor.YELLOW}$formattedAmount ${ChatColor.GREEN}tokens to ${ChatColor.WHITE}${user.getUsername()}${ChatColor.GREEN}'s balance.")
    }

}
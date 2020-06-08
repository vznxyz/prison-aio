package net.evilblock.prisonaio.module.user.command

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.NumberFormat

object TokensCommand {

    @Command(
        names = ["token", "tokens", "token balance", "token bal", "tokens balance", "tokens bal"],
        description = "Check your tokens balance",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") user: User) {
        val context = if (sender is Player && user.uuid == sender.uniqueId) {
            "${ChatColor.RED}Your tokens balance: "
        } else {
            "${ChatColor.RED}${user.getUsername()}'s tokens balance: "
        }

        FancyMessage(context)
            .then("${ChatColor.GRAY}${NumberUtils.format(user.getTokensBalance())}")
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Exact balance: ${NumberFormat.getInstance().format(user.getTokensBalance())}"))
            .send(sender)
    }

}
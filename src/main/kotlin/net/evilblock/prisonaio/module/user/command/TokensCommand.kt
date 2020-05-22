package net.evilblock.prisonaio.module.user.command

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.NumberFormat

object TokensCommand {

    @Command(names = ["token", "tokens", "token balance", "token bal", "tokens balance", "tokens bal"], description = "Check your tokens balance")
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)

        FancyMessage("${ChatColor.RED}Your tokens balance: ")
            .then("${ChatColor.GRAY}${NumberUtils.format(user.getTokensBalance())}")
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Exact balance: ${NumberFormat.getInstance().format(user.getTokensBalance())}"))
            .send(player)
    }

}
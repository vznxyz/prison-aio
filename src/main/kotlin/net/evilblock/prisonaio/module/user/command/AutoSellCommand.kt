package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object AutoSellCommand {

    @Command(
        names = ["auto-sell", "autosell"],
        description = "Toggles your auto-sell perk, if available"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (!user.perks.hasPerk(player, Perk.AUTO_SELL)) {
            player.sendMessage("${ChatColor.RED}You don't have access to auto-sell.")
            return
        }

        user.perks.togglePerk(Perk.AUTO_SELL)

        if (user.perks.isPerkEnabled(Perk.AUTO_SELL)) {
            player.sendMessage("${ChatColor.GREEN}Auto-sell is now enabled.")
        } else {
            player.sendMessage("${ChatColor.RED}Auto-sell is now disabled.")
        }
    }

}
package net.evilblock.prisonaio.module.crate.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.module.crate.key.CrateKey
import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object CrateGiveKeyToCommand {

    @Command(
        names = ["crate givekey to", "prison crate givekey to"],
        description = "Gives crate keys to a player",
        permission = "prisonaio.crates.givekey",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender,
                @Param(name = "player") player: Player,
                @Param(name = "crate") crate: Crate,
                @Param(name = "amount", defaultValue = "1") amount: Int,
                @Param(name = "reason", defaultValue = "Unspecified", wildcard = true) reason: String) {
        if (amount !in 1..64) {
            sender.sendMessage("${ChatColor.RED}You can only give up 1-64 crate keys at a time.")
            return
        }

        val issuedBy: UUID? = if (sender is Player) {
            sender.uniqueId
        } else {
            null
        }

        try {
            CrateKeyHandler.giveKey(player, crate, amount, issuedBy, reason)
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Failed to give key to ${player.name}.")
            return
        }

        if (sender is Player) {
            player.sendMessage("${CratesModule.getChatPrefix()}You were given ${amount}x ${crate.name} ${ChatColor.GRAY}keys by ${ChatColor.YELLOW}${sender.name}${ChatColor.GRAY}.")
        } else {
            player.sendMessage("${CratesModule.getChatPrefix()}You were given ${amount}x ${crate.name} ${ChatColor.GRAY}keys.")
        }

        sender.sendMessage("${CratesModule.getChatPrefix()}You've given ${amount}x ${crate.name} ${ChatColor.GRAY}keys to ${ChatColor.YELLOW}${player.name}${ChatColor.GRAY}.")
    }

}
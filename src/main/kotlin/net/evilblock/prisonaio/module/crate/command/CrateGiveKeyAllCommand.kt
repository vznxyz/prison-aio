/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object CrateGiveKeyAllCommand {

    @Command(
        names = ["crate givekey all", "prison crate givekey all"],
        description = "Gives crate keys to all players",
        permission = "prisonaio.crates.givekey.all"
    )
    @JvmStatic
    fun execute(sender: CommandSender,
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

        for (player in Bukkit.getOnlinePlayers()) {
            val message = StringBuilder()
            if (sender is Player) {
                message.append("${CratesModule.getChatPrefix()}You were given ${amount}x ${crate.name} ${ChatColor.GRAY}keys by ${ChatColor.YELLOW}${sender.name}${ChatColor.GRAY}.")
            } else {
                message.append("${CratesModule.getChatPrefix()}You were given ${amount}x ${crate.name} ${ChatColor.GRAY}keys.")
            }

            val keyItemStack = CrateKeyHandler.makeKeyItemStack(player, crate, amount, issuedBy, reason)

            if (player.inventory.firstEmpty() == -1) {
                player.enderChest.addItem(keyItemStack)
                message.append("${ChatColor.RED}${ChatColor.BOLD}(ADDED TO ENDERCHEST)")
            } else {
                player.inventory.addItem(keyItemStack)
            }

            player.sendMessage(message.toString())
            player.updateInventory()
        }

        sender.sendMessage("${CratesModule.getChatPrefix()}You've given ${amount}x ${crate.name} ${ChatColor.GRAY}keys to ${ChatColor.YELLOW}${Bukkit.getOnlinePlayers().size} players${ChatColor.GRAY}.")
    }

}
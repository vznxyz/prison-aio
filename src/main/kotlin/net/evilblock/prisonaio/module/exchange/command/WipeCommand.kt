/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.exchange.GrandExchangeUserData
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

object WipeCommand {

    @Command(
        names = ["grandexchange wipe", "ge wipe", "grand-exchange wipe"],
        description = "Wipes all the Grand Exchange listings",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender !is ConsoleCommandSender) {
            sender.sendMessage("${ChatColor.RED}This command must be executed through console.")
            return
        }

        try {
            val wiped = GrandExchangeHandler.wipeListings()
            sender.sendMessage("${ChatColor.GREEN}Successfully wiped ${NumberUtils.format(wiped)} listings from the Grand Exchange!")
        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendMessage("${ChatColor.RED}Failed to wipe the Grand Exchange listings!")
        }
    }

    @Command(
        names = ["grandexchange wipe", "ge wipe", "grand-exchange wipe"],
        description = "Wipes a user's Grand Exchange data",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "user") user: User) {
        user.grandExchangeData = GrandExchangeUserData(user)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}You have reset ${ChatColor.WHITE}${user.getUsername()} ${ChatColor.GREEN}'s Grand Exchange user data!")
    }

}
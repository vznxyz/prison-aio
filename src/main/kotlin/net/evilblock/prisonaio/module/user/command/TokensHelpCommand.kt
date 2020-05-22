package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

object TokensHelpCommand {

    @Command(names = ["token help", "tokens help"], description = "Helpful information about tokens")
    @JvmStatic
    fun execute(sender: CommandSender) {
        val messages = arrayListOf<String>()

        if (sender is Player) {
            messages.add("${ChatColor.RED}${ChatColor.BOLD}Tokens Help")
            messages.add("${ChatColor.GRAY}/tokens withdraw <player> - ${ChatColor.RESET}Withdraw tokens to your inventory")
        }

        if (sender is ConsoleCommandSender || sender.hasPermission(Permissions.TOKENS_ADMIN)) {
            if (messages.isNotEmpty()) {
                messages.add("")
            }

            messages.add("${ChatColor.RED}${ChatColor.BOLD}Tokens Admin Help")
            messages.add("${ChatColor.GRAY}/tokens give <player> <amount> - ${ChatColor.RESET}Adds tokens to a player's balance")
            messages.add("${ChatColor.GRAY}/tokens take <player> <amount> - ${ChatColor.RESET}Takes tokens from a player's balance")
            messages.add("${ChatColor.GRAY}/tokens set <player> <amount> - ${ChatColor.RESET}Sets a player's tokens balance")
            messages.add("${ChatColor.GRAY}/tokens reset <player> - ${ChatColor.RESET}Resets a player's tokens balance")
        }

        messages.add(0, "")
        messages.add("")

        messages.forEach { message -> sender.sendMessage(message) }
    }

}
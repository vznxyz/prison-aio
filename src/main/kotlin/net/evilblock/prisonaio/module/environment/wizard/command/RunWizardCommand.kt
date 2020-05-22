package net.evilblock.prisonaio.module.environment.wizard.command

import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RunWizardCommand {

    @Command(
        names = ["prison wizard"],
        description = "Runs production environment wizard",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage("${ChatColor.GREEN}Running production environment checks...")
        sender.sendMessage("")



        sender.sendMessage("")
        sender.sendMessage("${ChatColor.GREEN}Finished checks!")
    }

}
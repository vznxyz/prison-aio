package net.evilblock.prisonaio.module.storage.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.storage.StorageModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object WipeDatabaseCommand {

    @Command(
        names = ["prison wipedb"],
        description = "Wipe the PrisonAIO database",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender is Player) {
            sender.sendMessage("${ChatColor.RED}That command must be executed from console!")
            return
        }

        StorageModule.database.drop()
        sender.sendMessage("${ChatColor.GREEN}Successfully wiped database!")
    }

}
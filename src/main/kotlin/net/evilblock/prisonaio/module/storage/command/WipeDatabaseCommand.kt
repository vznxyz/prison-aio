package net.evilblock.prisonaio.module.storage.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.storage.StorageModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object WipeDatabaseCommand {

    @Command(
        names = ["prison wipedb"],
        description = "Wipe the PrisonAIO database",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        StorageModule.database.drop()
        sender.sendMessage("${ChatColor.GREEN}Successfully wiped database!")
    }

}
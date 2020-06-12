package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object MineListCommand {

    @Command(
        names = ["mine list"],
        description = "Lists all of the mine IDs",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage(MineHandler.getMines().joinToString { it.id })
    }

}
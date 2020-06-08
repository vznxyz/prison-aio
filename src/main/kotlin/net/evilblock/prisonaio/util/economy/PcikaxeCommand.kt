package net.evilblock.prisonaio.util.economy

import net.evilblock.cubed.command.Command
import org.bukkit.command.CommandSender

object PcikaxeCommand {

    @Command(names = ["pcikaxe"])
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage("yes")
    }

}
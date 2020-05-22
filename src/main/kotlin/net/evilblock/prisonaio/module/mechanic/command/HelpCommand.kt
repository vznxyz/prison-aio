package net.evilblock.prisonaio.module.mechanic.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.util.Constants
import org.bukkit.command.CommandSender

object HelpCommand {

    @Command(
        names = ["help", "prison help"],
        description = "Helpful information about our server"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        for (line in MechanicsModule.getHelpMessages()) {
            sender.sendMessage(line.replace("{LONG_LINE}", Constants.LONG_LINE))
        }
    }

}
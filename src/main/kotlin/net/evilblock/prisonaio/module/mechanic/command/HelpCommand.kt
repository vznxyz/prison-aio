/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
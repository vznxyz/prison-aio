/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
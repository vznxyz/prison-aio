/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.reward.multiplier.MultiplierEventHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object MultiplierEventEndCommand {

    @Command(
        names = ["multi-event end"],
        description = "End an active multiplier event",
        permission = Permissions.MULTI_EVENT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (!MultiplierEventHandler.isEventActive()) {
            sender.sendMessage("${ChatColor.RED}There is no active multiplier event!")
            return
        }

        MultiplierEventHandler.endEvent(MultiplierEventHandler.getActiveEvent()!!, true)
    }

}
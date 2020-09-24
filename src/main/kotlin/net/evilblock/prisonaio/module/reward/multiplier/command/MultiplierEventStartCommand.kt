/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.Duration
import net.evilblock.prisonaio.module.reward.multiplier.MultiplierEvent
import net.evilblock.prisonaio.module.reward.multiplier.MultiplierEventHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object MultiplierEventStartCommand {

    @Command(
        names = ["multi-event start"],
        description = "Start a multiplier event",
        permission = Permissions.MULTI_EVENT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "multi") multiplier: Double, @Param(name = "duration") duration: Duration) {
        if (MultiplierEventHandler.isEventActive()) {
            sender.sendMessage("${ChatColor.RED}There is already an active multiplier event!")
            return
        }

        if (duration.isPermanent()) {
            sender.sendMessage("${ChatColor.RED}You can't start a multiplier event that never expires!")
            return
        }

        MultiplierEventHandler.startEvent(MultiplierEvent(multi = multiplier, expires = System.currentTimeMillis() + duration.get()))
    }

}
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
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplier
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierType
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GlobalMultiplierStartCommand {

    @Command(
        names = ["global-multi start"],
        description = "Start a global multiplier",
        permission = Permissions.GLOBAL_MULTIPLIER,
        async = true
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "type") type: GlobalMultiplierType,
        @Param(name = "multi") multiplier: Double,
        @Param(name = "duration") duration: Duration
    ) {
        val activeEvent = GlobalMultiplierHandler.getEvent(type)
        if (activeEvent != null) {
            sender.sendMessage("${ChatColor.RED}There is an ongoing ${type.displayName} event!")
            return
        }

        if (duration.isPermanent()) {
            sender.sendMessage("${ChatColor.RED}A global multiplier must expire!")
            return
        }

        GlobalMultiplierHandler.startEvent(GlobalMultiplier(type = type, multiplier = multiplier, expires = System.currentTimeMillis() + duration.get()))
    }

}
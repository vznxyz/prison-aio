/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierType
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GlobalMultiplierStopCommand {

    @Command(
        names = ["global-multi stop"],
        description = "Stops a global multiplier",
        permission = Permissions.GLOBAL_MULTIPLIER,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "type") type: GlobalMultiplierType) {
        val event = GlobalMultiplierHandler.getEvent(type)
        if (event == null) {
            sender.sendMessage("${ChatColor.RED}There is no active ${type.displayName} event!")
            return
        }

        GlobalMultiplierHandler.endEvent(event, true)
    }

}
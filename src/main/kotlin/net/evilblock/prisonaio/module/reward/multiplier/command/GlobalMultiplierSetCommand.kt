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
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GlobalMultiplierSetCommand {

    @Command(
        names = ["global-multi set"],
        description = "Sets the global multiplier",
        permission = Permissions.GLOBAL_MULTIPLIER,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "multi") multiplier: Double, @Param(name = "duration") duration: Duration) {
        if (GlobalMultiplierHandler.isSet()) {
            sender.sendMessage("${ChatColor.RED}A global multiplier is already set!")
            return
        }

        if (duration.isPermanent()) {
            sender.sendMessage("${ChatColor.RED}You can't set a global multiplier that never expires!")
            return
        }

        GlobalMultiplierHandler.setActiveMultiplier(GlobalMultiplier(multiplier = multiplier, expires = System.currentTimeMillis() + duration.get()))
    }

}
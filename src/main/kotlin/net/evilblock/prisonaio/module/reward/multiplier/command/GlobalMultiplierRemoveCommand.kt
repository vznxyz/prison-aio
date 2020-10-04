/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GlobalMultiplierRemoveCommand {

    @Command(
        names = ["global-multi remove"],
        description = "Removes the global multiplier",
        permission = Permissions.GLOBAL_MULTIPLIER,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (!GlobalMultiplierHandler.isSet()) {
            sender.sendMessage("${ChatColor.RED}There is no global multiplier set!")
            return
        }

        GlobalMultiplierHandler.endEvent(GlobalMultiplierHandler.getActiveMultiplier()!!, true)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GangRefreshValueCommand {

    @Command(
        names = ["gang admin recalc-values", "gangs admin recalc-values"],
        description = "Recalculate each gang's value",
        permission = Permissions.GANGS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        var count = 0
        for (gang in GangHandler.getAllGangs()) {
            gang.updateCachedCellValue()
            count++
        }

        sender.sendMessage("${ChatColor.GREEN}Recalculated $count gangs!")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.analytic.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.system.analytic.Analytic
import net.evilblock.prisonaio.module.system.analytic.AnalyticHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object WipeAnalyticsCommand {

    @Command(
        names = ["prison analytics wipe"],
        description = "Resets all tracked analytics data",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        Analytic.values().forEach { it.updateValue(it.defaultValue) }
        AnalyticHandler.saveData()

        sender.sendMessage("${ChatColor.GREEN}Successfully wiped tracked analytics.")
    }

}
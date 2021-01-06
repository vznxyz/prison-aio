/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.logging.ErrorHandler
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object SaveCommand {

    @Command(
        names = ["prison save"],
        description = "Save all PrisonAIO data",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        for (world in Bukkit.getWorlds()) {
            try {
                Tasks.sync {
                    world.save()
                }
            } catch (exception: Exception) {
                ErrorHandler.generateErrorLog(
                    errorType = "saveWorld",
                    event = mapOf("WorldName" to world.name),
                    exception = exception
                )

                PrisonAIO.instance.systemLog("${ChatColor.RED}Failed to save world ${world.name}!")
            }
        }

        PrisonAIO.instance.saveModules()
    }

}
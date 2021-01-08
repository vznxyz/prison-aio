/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.robot.menu.PlotRobotsMenu
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ToggleMergeCommand {

    @Command(
            names = ["robots toggle-merge", "robot toggle-merge"],
            description = "Toggles the Robot Merge functionality",
            permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        PlotRobotsMenu.disabled = !PlotRobotsMenu.disabled

        if (PlotRobotsMenu.disabled) {
            sender.sendMessage("${ChatColor.RED}Robot Merge functionality disabled!")
        } else {
            sender.sendMessage("${ChatColor.GREEN}Robot Merge functionality enabled!")
        }
    }

}
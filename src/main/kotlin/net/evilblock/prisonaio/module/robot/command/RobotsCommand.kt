/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.menu.PlotRobotsMenu
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RobotsCommand {

    @Command(
        names = ["robot", "robots"],
        description = "Opens the Plot Robots GUI"
    )
    @JvmStatic
    fun execute(player: Player) {
        val plot = PlotUtil.getPlot(player.location)

        if (RegionBypass.hasBypass(player)) {
            RegionBypass.attemptNotify(player)
        } else {
            if (!RobotHandler.isPrivileged(player, player.location)) {
                return
            }

            // check if placing on plot if player doesn't have bypass
            if (plot == null) {
                player.sendMessage("${ChatColor.RED}You must be standing on a plot you have access to!")
                return
            }

            PlotRobotsMenu(plot).openMenu(player)
        }
    }

}
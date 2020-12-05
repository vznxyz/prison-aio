/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.generator.menu.PanelMenu
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GeneratorCommand {

    @Command(
        names = ["generator", "gen", "jenerator", "core"],
        description = "Opens the Generator Panel"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (!canAccess(player)) {
            player.sendMessage("${ChatColor.RED}You must be standing on a plot you own to view your Generator Panel!")
            return
        }

        val plot = PlotUtil.getPlot(player.location) ?: throw IllegalAccessException("Plot is null")
        PanelMenu(plot).openMenu(player)
    }

    private fun canAccess(player: Player): Boolean {
        val plot = PlotUtil.getPlot(player.location) ?: return false

        if (RegionBypass.hasBypass(player)) {
            RegionBypass.attemptNotify(player)
            return true
        }

        if (plot.isOwner(player.uniqueId)) {
            return true
        }

        return false
    }

}
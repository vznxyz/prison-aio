/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.analytic.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.system.analytic.menu.AnalyticsMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object AnalyticsCommand {

    @Command(
        names = ["prison analytics"],
        description = "Opens the Analytics menu",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        AnalyticsMenu().openMenu(player)
    }

}
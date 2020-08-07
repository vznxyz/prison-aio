/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.menu.HomesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangHomesCommand {

    @Command(
        names = ["gang homes", "gangs homes"],
        description = "Show all of the gangs you have access to"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (GangHandler.getAccessibleGangs(player.uniqueId).isEmpty()) {
            player.sendMessage("${ChatColor.RED}You aren't a member of any gangs.")
            return
        }

        HomesMenu().openMenu(player)
    }

}
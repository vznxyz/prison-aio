/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.cell.menu.HomesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellHomesCommand {

    @Command(
        names = ["cell homes", "cells homes"],
        description = "Show all of the cells you have access to"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CellHandler.getJoinableCells(player.uniqueId).isEmpty()) {
            player.sendMessage("${ChatColor.RED}You aren't a member of any cells.")
            return
        }

        HomesMenu().openMenu(player)
    }

}
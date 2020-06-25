/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command.admin

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object CellForceKickCommand {

    @Command(
        names = ["cell forcekick", "cells forcekick"],
        description = "Force kick a player from a cell",
        permission = Permissions.CELLS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "cell") cell: Cell, @Param(name = "player") playerUuid: UUID) {
        if (!cell.isMember(playerUuid)) {
            player.sendMessage("${ChatColor.RED}That player is not a member of the cell.")
            return
        }

        cell.kickMember(playerUuid)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        player.sendMessage("${ChatColor.GREEN}Successfully kicked $playerName from the cell.")
    }

}
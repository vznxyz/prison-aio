/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object CellSetLeaderCommand {

    @Command(
        names = ["cell leader", "cells leader"],
        description = "Create a new cell",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") newOwner: UUID) {
        val cell = CellHandler.getAssumedCell(player.uniqueId)
        if (cell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to set relinquish ownership of it.")
            return
        }

        if (!cell.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the owner can relinquish ownership of the cell.")
            return
        }

        if (player.uniqueId == newOwner) {
            player.sendMessage("${ChatColor.RED}You are already the owner of your cell.")
            return
        }

        val newOwnerUsername = Cubed.instance.uuidCache.name(newOwner)

        if (!cell.isMember(newOwner)) {
            player.sendMessage("${ChatColor.RED}$newOwnerUsername is not a member of your cell.")
            return
        }

        cell.updateOwner(newOwner)
        player.sendMessage("${ChatColor.GREEN}You have given ownership of your cell to $newOwnerUsername.")
    }

}
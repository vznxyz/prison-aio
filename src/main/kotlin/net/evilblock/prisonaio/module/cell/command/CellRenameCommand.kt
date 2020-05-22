package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellRenameCommand {

    @Command(
        names = ["cell rename", "cells rename"],
        description = "Rename your cell"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name", wildcard = true) name: String) {
        val visitingCell = CellHandler.getVisitingCell(player)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to rename it.")
            return
        }

        if (!visitingCell.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the owner can rename the cell.")
            return
        }

        visitingCell.updateName(player, name)
    }

}
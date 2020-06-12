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
        val cell = CellHandler.getAssumedCell(player.uniqueId)
        if (cell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to rename it.")
            return
        }

        for (blockedName in CellHandler.BLOCKED_NAMES) {
            if (blockedName.matches(name)) {
                player.sendMessage("${ChatColor.RED}The name you input contains inappropriate content. Please try a different name.")
                return
            }
        }

        if (!cell.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the owner can rename the cell.")
            return
        }

        if (CellHandler.getCellByName(name) != null) {
            player.sendMessage("${ChatColor.RED}The name `$name` is already taken by another cell.")
            return
        }

        CellHandler.renameCell(cell, name)
        cell.sendMessages("${ChatColor.YELLOW}The cell has been renamed to `$name` by ${player.name}!")
    }

}
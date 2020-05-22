package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellSetHomeCommand {

    @Command(names = ["cell sethome", "cells sethome"], description = "Set the home location of your cell")
    @JvmStatic
    fun execute(player: Player) {
        val visitingCell = CellHandler.getVisitingCell(player)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to set its home location.")
            return
        }

        if (visitingCell.owner != player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Only the owner of the cell can set the home location.")
            return
        }

        if (!visitingCell.cuboid.contains(player.location)) {
            player.sendMessage("${ChatColor.RED}You can only set the home location to somewhere within the cell.")
            return
        }

        visitingCell.homeLocation = player.location
        visitingCell.sendMessages("${ChatColor.YELLOW}${player.name} updated the cell's home location.")

        player.sendMessage("${ChatColor.GREEN}Successfully updated the cell's home location.")
    }

}
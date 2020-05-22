package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellSetAnnouncementCommand {

    @Command(
        names = ["cell announcement", "cell setannouncement", "cells announcement", "cells setannouncement"],
        description = "Set your cell's announcement"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "announcement", wildcard = true) announcement: String) {
        val visitingCell = CellHandler.getVisitingCell(player)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to update its announcement.")
            return
        }

        if (visitingCell.owner != player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Only the owner can update the cell's announcement.")
            return
        }

        visitingCell.updateAnnouncement(sender = player, announcement = announcement)

        player.sendMessage("${ChatColor.GREEN}Successfully updated the cell's announcement!")
    }

}
package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellLeaveCommand {

    @Command(names = ["cell leave", "cells leave"], description = "Leave a cell", async = true)
    @JvmStatic
    fun execute(player: Player) {
        val visitingCell = CellHandler.getVisitingCell(player)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to leave it.")
            return
        }

        if (visitingCell.owner == player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Owners must disband their cell instead of leaving. Try `/cell disband`.")
            return
        }

        visitingCell.memberLeave(player.uniqueId)

        val ownerName = Cubed.instance.uuidCache.name(visitingCell.owner)
        player.sendMessage("${ChatColor.GREEN}Successfully left $ownerName's cell.")
    }

}
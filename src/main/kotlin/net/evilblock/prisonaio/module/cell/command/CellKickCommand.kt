package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object CellKickCommand {

    @Command(names = ["cell kick", "cells kick"], description = "Kick a player from your cell", async = true)
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") playerUuid: UUID) {
        val visitingCell = CellHandler.getVisitingCell(player)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to kick a player from it.")
            return
        }

        if (visitingCell.owner != player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Only the owner can kick players from the cell.")
            return
        }

        if (player.uniqueId == playerUuid) {
            player.sendMessage("${ChatColor.RED}You can't kick yourself from the cell.")
            return
        }

        if (visitingCell.isOwner(playerUuid)) {
            player.sendMessage("${ChatColor.RED}You can't kick the owner from their own cell.")
            return
        }

        if (!visitingCell.isMember(playerUuid)) {
            player.sendMessage("${ChatColor.RED}That player is not a member of the cell.")
            return
        }

        visitingCell.kickMember(playerUuid)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        player.sendMessage("${ChatColor.GREEN}Successfully kicked $playerName from the cell.")
    }

}
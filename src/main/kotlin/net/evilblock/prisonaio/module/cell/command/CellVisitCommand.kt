package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object CellVisitCommand {

    @Command(
        names = ["cell visit", "cell v", "cells visit", "cells v"],
        description = "Visit another player's cell"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") targetUuid: UUID) {
        if (player.uniqueId == targetUuid) {
            player.sendMessage("${ChatColor.RED}If you want to join your own island, use the `/cell join` command.")
            return
        }

        val cellsOwnedByTarget = CellHandler.getOwnedCells(targetUuid)
        if (cellsOwnedByTarget.isEmpty()) {
            val targetName = Cubed.instance.uuidCache.name(targetUuid)
            player.sendMessage("${ChatColor.RED}$targetName doesn't seem to own a cell.")
            return
        }

        // attemptJoinSession handles the ALLOW_VISITORS permission test so we don't have to
        CellHandler.attemptJoinSession(player, cellsOwnedByTarget.first())
    }

}
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

object CellRevokeInviteCommand {

    @Command(names = ["cell revokeinvite", "cells revokeinvite"], description = "Revoke a player's invitation to join")
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") playerUuid: UUID) {
        val visitingCell = CellHandler.getVisitingCell(sender)
        if (visitingCell == null) {
            sender.sendMessage("${ChatColor.RED}You must be inside a cell to revoke player invitations.")
            return
        }

        if (visitingCell.owner != sender.uniqueId) {
            sender.sendMessage("${ChatColor.RED}Only the owner can revoke player invitations.")
            return
        }

        if (!visitingCell.isInvited(playerUuid)) {
            sender.sendMessage("${ChatColor.RED}That player hasn't been invited to join the cell.")
            return
        }

        visitingCell.revokeInvite(playerUuid)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        sender.sendMessage("${ChatColor.GREEN}Successfully revoked $playerName's invitation.")
    }

}
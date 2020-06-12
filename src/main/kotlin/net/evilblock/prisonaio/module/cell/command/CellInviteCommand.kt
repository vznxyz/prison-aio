package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object CellInviteCommand {

    @Command(
        names = ["cell invite", "cells invite"],
        description = "Invite a player to your cell"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") playerUuid: UUID) {
        val cell = CellHandler.getAssumedCell(sender.uniqueId)
        if (cell == null) {
            sender.sendMessage("${ChatColor.RED}You must be inside your cell to invite other players.")
            return
        }

        if (cell.owner != sender.uniqueId) {
            sender.sendMessage("${ChatColor.RED}Only the owner of the cell can invite other players.")
            return
        }

        if (cell.isMember(playerUuid)) {
            sender.sendMessage("${ChatColor.RED}That player is already a member of the cell.")
            return
        }

        if (cell.isInvited(playerUuid)) {
            sender.sendMessage("${ChatColor.RED}That player has already been invited to the cell.")
            return
        }

        cell.invitePlayer(playerUuid, sender.uniqueId)

        val playerInvitedName = Cubed.instance.uuidCache.name(playerUuid)
        sender.sendMessage("${ChatColor.GREEN}Successfully invited $playerInvitedName to the cell.")
    }

}
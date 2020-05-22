package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.cell.CellsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellJoinCommand {

    @Command(names = ["cell join", "cells join", "cell accept", "cells accept"], description = "Join a cell you've been invited to")
    @JvmStatic
    fun execute(player: Player, @Param(name = "cellId") cell: Cell) {
        if (cell.isMember(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You are already a member of that cell.")
            return
        }

        if (!cell.isInvited(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You haven't been invited to join that cell.")
            return
        }

        if (cell.getMembers().size >= CellsModule.getMaxMembers()) {
            player.sendMessage("${ChatColor.RED}That cell has the maximum amount of members. Somebody will have to leave or be kicked for you to be able to join.")
            return
        }

        cell.memberJoin(player.uniqueId)

        val ownerName = Cubed.instance.uuidCache.name(cell.owner)
        player.sendMessage("${ChatColor.GREEN}Successfully joined $ownerName's cell.")

        CellHandler.attemptJoinSession(player, cell)
    }

}
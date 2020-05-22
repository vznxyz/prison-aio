package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.cell.CellsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellCreateCommand {

    @Command(
        names = ["cell create", "cells create"],
        description = "Create a new cell",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name", wildcard = true) name: String) {
        if (CellHandler.getOwnedCells(player.uniqueId).isNotEmpty()) {
            player.sendMessage("${ChatColor.RED}You can only have one cell at a time. To generate a new cell, delete your old cell and then try again.")
            return
        }

        if (name.length > CellsModule.getMaxNameLength()) {
            player.sendMessage("${ChatColor.RED}A cell's name can only be 32 characters long. The name you entered was ${name.length} characters.")
            return
        }

        player.sendMessage("${ChatColor.GREEN}Generating a new cell for you...")

        try {
            CellHandler.createNewCell(player.uniqueId, name) { cell ->
                PrisonAIO.instance.server.scheduler.runTask(PrisonAIO.instance) {
                    CellHandler.attemptJoinSession(player, cell)
                    player.sendMessage("${ChatColor.GREEN}You are now the owner of this cell. Use ${ChatColor.YELLOW}/cell home ${ChatColor.GREEN}to teleport back to your cell.")
                }
            }
        } catch (e: Exception) {
            if (player.isOp) {
                player.sendMessage("${ChatColor.RED}Failed to generate a new cell for you: ${e.message}")
            } else {
                player.sendMessage("${ChatColor.RED}Failed to generate a new cell for you. If this issue persists, please contact an administrator.")
            }
            return
        }
    }

}
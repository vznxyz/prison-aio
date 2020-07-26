/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
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
        for (blockedName in CellHandler.BLOCKED_NAMES) {
            if (blockedName.matches(name)) {
                player.sendMessage("${ChatColor.RED}The name you input contains inappropriate content. Please try a different name.")
                return
            }
        }

        if (!name.matches(EzPrompt.IDENTIFIER_REGEX)) {
            player.sendMessage("${ChatColor.RED}The name you input does not match the regex pattern ${EzPrompt.IDENTIFIER_REGEX.pattern}.")
            return
        }

        if (CellHandler.getOwnedCells(player.uniqueId).isNotEmpty()) {
            player.sendMessage("${ChatColor.RED}You can only have one cell at a time. To generate a new cell, delete your old cell and then try again.")
            return
        }

        if (CellHandler.getCellByName(name) != null) {
            player.sendMessage("${ChatColor.RED}A cell with the name `$name` already exists.")
            return
        }

        if (name.length > CellsModule.getMaxNameLength()) {
            player.sendMessage("${ChatColor.RED}A cell's name can only be 32 characters long. The name you entered was ${name.length} characters.")
            return
        }

        player.sendMessage("${ChatColor.GREEN}Generating a new cell for you...")

        try {
            CellHandler.createNewCell(player.uniqueId, name) { cell ->
                Tasks.sync {
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
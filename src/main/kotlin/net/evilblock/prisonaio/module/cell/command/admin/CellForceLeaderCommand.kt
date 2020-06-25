/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.command.admin

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object CellForceLeaderCommand {

    @Command(
        names = ["cell admin force-leader", "cells admin force-leader"],
        description = "Force disband a cell",
        permission = Permissions.CELLS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "cell") cell: Cell, @Param(name = "player") newOwner: UUID) {
        val newOwnerUsername = Cubed.instance.uuidCache.name(newOwner)
        if (cell.isOwner(newOwner)) {
            sender.sendMessage("${ChatColor.RED}$newOwnerUsername is already the owner of ${cell.name}.")
            return
        }

        if (!cell.isMember(newOwner)) {
            sender.sendMessage("${ChatColor.RED}$newOwnerUsername is not a member of ${cell.name}.")
            return
        }

        cell.updateOwner(newOwner)
        sender.sendMessage("${ChatColor.GREEN}You have given ownership of ${cell.name} to $newOwnerUsername.")
    }

}
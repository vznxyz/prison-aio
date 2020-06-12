package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.cell.menu.HomesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellHomeCommand {

    @Command(names = ["cell home", "cells home"], description = "Teleport to your cell home")
    @JvmStatic
    fun execute(player: Player) {
        val joinableCells = CellHandler.getJoinableCells(player.uniqueId)
        if (joinableCells.isEmpty()) {
            player.sendMessage("${ChatColor.RED}You aren't a member of any cells.")
            return
        }

        if (joinableCells.size > 1) {
            HomesMenu().openMenu(player)
            return
        }

        player.sendMessage("${ChatColor.GREEN}Teleporting you to your cell...")

        val cell = joinableCells.first()
        CellHandler.attemptJoinSession(player, cell)
    }

}
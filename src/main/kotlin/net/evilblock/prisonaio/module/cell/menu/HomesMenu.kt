/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class HomesMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "Your Cells"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (cell in CellHandler.getJoinableCells(player.uniqueId).sortedBy { if (it.owner == player.uniqueId) 0 else 1 }) {
            buttons[buttons.size] = CellButton(cell)
        }

        return buttons
    }

    private inner class CellButton(private val cell: Cell) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}${cell.name}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}Owned by ${cell.getOwnerUsername()}")
            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Current Session")

            if (cell.getActiveMembers().isEmpty()) {
                description.add("${ChatColor.GRAY}Nobody is playing right now")
            } else {
                for (activePlayer in cell.getActiveMembers()) {
                    description.add(" ${ChatColor.RESET}${activePlayer.name}")
                }
            }

            description.add("")
            description.add("${ChatColor.YELLOW}Click to join session.")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.GRASS
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (cell.isMember(player.uniqueId)) {
                    CellHandler.attemptJoinSession(player, cell)
                }
            }
        }
    }

}
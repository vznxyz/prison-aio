/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.mine.Mine
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class MineEditMenu(private val mine: Mine) : Menu() {

    override fun getTitle(player: Player): String {
        return "Mine Editor - ${mine.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[11] = ManageBlocksButton(mine)
        buttons[13] = ManageResetButton(mine)
        buttons[15] = MineTeleportButton(mine)

        // make empty row on bottom
        buttons[26] = Button.placeholder(Material.AIR)

        return buttons
    }

    private inner class ManageBlocksButton(private val mine: Mine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Manage Blocks"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Manage this mine's block types."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.STONE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            MineManageBlocksMenu(mine).openMenu(player)
        }
    }

    private inner class ManageResetButton(private val mine: Mine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Manage Reset"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Force reset or change how",
                "${ChatColor.GRAY}this mine resets."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.DISPENSER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            MineManageResetMenu(mine).openMenu(player)
        }
    }

    private inner class MineTeleportButton(private val mine: Mine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Teleport"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Teleport to this mine's spawn point."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (mine.spawnPoint == null) {
                player.sendMessage("${ChatColor.RED}Mine ${ChatColor.WHITE}${mine.id}${ChatColor.RED}'s spawn point has not been set.")
            } else {
                player.sendMessage("${ChatColor.GREEN}Teleporting you to ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}...")
                player.teleport(mine.spawnPoint)
            }
        }
    }

}
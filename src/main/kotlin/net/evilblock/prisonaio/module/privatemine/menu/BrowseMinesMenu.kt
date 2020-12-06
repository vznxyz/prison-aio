/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

abstract class BrowseMinesMenu : PaginatedMenu() {

    companion object {
        private val BLACK_SLOTS = listOf(
            0, 2, 4, 5, 6, 7, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
        )

        private val BUTTON_SLOTS = listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        )
    }

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[1] = MyMinesMenuButton()
        buttons[3] = PublicMinesMenuButton()

        for (i in BLACK_SLOTS) {
            buttons[i] = GlassButton(7)
        }

        return buttons
    }

    override fun getAllPagesButtonSlots(): List<Int> {
        return BUTTON_SLOTS
    }

    override fun getAutoUpdateTicks(): Long {
        return 500L
    }

    private inner class MyMinesMenuButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}My Private Mines"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "View the private mines that you have access to.")
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            AccessibleMinesMenu().openMenu(player)
        }
    }

    private inner class PublicMinesMenuButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}View Public Mines"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "View the private mines that are open to the public. Owners can set a sales tax (max 10%) on any blocks you sell to the mine's shop!")
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            PublicMinesMenu().openMenu(player)
        }
    }

}
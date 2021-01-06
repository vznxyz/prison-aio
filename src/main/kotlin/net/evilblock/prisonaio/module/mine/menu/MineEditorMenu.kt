/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PageButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.system.menu.PrisonManagementMenu
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class MineEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Mine Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            if (page == 1) {
                buttons[0] = BackButton { PrisonManagementMenu().openMenu(player) }
                buttons[8] = PageButton(1, this)
            } else {
                buttons[0] = PageButton(-1, this)
                buttons[8] = PageButton(1, this)
            }

            buttons[4] = InfoButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (mine in MineHandler.getMines()) {
                buttons[buttons.size] = MineButton(mine)
            }
        }
    }

    override fun getPageButtonSlots(): Pair<Int, Int> {
        return Pair(0, 8)
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Mines"
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_ORE
        }
    }

    private inner class MineButton(private val mine: Mine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${mine.id}"
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_ORE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                MineEditMenu(mine).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        MineHandler.deleteMine(mine)

                        Tasks.async {
                            MineHandler.saveData()
                        }
                    }

                    this@MineEditorMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}
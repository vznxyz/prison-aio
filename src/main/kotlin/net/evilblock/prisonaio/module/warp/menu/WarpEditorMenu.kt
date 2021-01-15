/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.warp.Warp
import net.evilblock.prisonaio.module.warp.WarpHandler
import net.evilblock.prisonaio.module.warp.category.menu.CategoryEditorMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class WarpEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Warp Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = EditCategoriesButton()

            for (i in 9..17) {
                buttons[i] = GlassButton(0)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (warp in WarpHandler.getWarps()) {
                buttons[buttons.size] = WarpButton(warp)
            }
        }
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class EditCategoriesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Categories"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Group warps into categories, for a better user experience."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit categories"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                CategoryEditorMenu().openMenu(player)
            }
        }
    }

    private inner class WarpButton(private val warp: Warp) : Button() {
        override fun getName(player: Player): String {
            return warp.getFormattedName()
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit warp"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditWarpMenu(warp).openMenu(player)
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.MenuButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.warp.Warp
import net.evilblock.prisonaio.module.warp.WarpHandler
import net.evilblock.prisonaio.module.warp.category.WarpCategory
import net.evilblock.prisonaio.module.warp.category.WarpCategoryHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class EditCategoryWarpsMenu(private val category: WarpCategory) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Warps - ${category.name}"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 1 until 8) {
                buttons[i] = GlassButton(0)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            if (WarpHandler.getWarps().isEmpty()) {
                return@also
            }

            val sorted = arrayListOf<Warp>().also { sorted ->
                sorted.addAll(WarpHandler.getWarps().filter { category.warps.contains(it) })
                sorted.addAll(WarpHandler.getWarps().filter { !category.warps.contains(it) }.sortedBy { it.id })
            }

            for (warp in sorted) {
                buttons[buttons.size] = MenuButton()
                    .icon(Material.ENDER_PEARL)
                    .name(warp.getFormattedName())
                    .lore(arrayListOf<String>().also { desc ->
                        desc.add("")

                        if (category.warps.contains(warp)) {
                            desc.add(styleAction(ChatColor.RED, "LEFT-CLICK", "to un-assign warp"))
                        } else {
                            desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to assign warp"))
                        }
                    })
                    .glow { category.warps.contains(warp) }
                    .action(ClickType.LEFT) {
                        if (category.warps.contains(warp)) {
                            category.warps.remove(warp)
                        } else {
                            category.warps.add(warp)
                        }

                        Tasks.async {
                            WarpCategoryHandler.saveData()
                        }
                    }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditCategoryMenu(category).openMenu(player)
            }
        }
    }

}
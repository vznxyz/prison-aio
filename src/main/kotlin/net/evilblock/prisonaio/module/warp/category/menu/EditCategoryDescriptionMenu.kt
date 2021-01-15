/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.warp.category.WarpCategory
import net.evilblock.prisonaio.module.warp.category.WarpCategoryHandler
import org.bukkit.entity.Player

class EditCategoryDescriptionMenu(private val category: WarpCategory) : TextEditorMenu(lines = category.description) {

    init {
        updateAfterClick = true
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Description - ${category.name}"
    }

    override fun onSave(player: Player, list: List<String>) {
        category.description = list.toMutableList()

        Tasks.async {
            WarpCategoryHandler.saveData()
        }
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditCategoryMenu(category).openMenu(player)
        }
    }

}
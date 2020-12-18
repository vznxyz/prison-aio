/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warps.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.warps.Warp
import net.evilblock.prisonaio.module.warps.WarpHandler
import org.bukkit.entity.Player

class WarpEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Warp Editor"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (warp in WarpHandler.getWarps()) {
                buttons[buttons.size] = WarpButton(warp)
            }
        }
    }

    private inner class WarpButton(private val warp: Warp) : Button() {

    }

}
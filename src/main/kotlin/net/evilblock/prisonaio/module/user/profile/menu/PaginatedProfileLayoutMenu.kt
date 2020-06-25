/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.profile.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import org.bukkit.entity.Player

abstract class PaginatedProfileLayoutMenu(protected val layout: ProfileLayout) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return layout.renderTitle(player)
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return layout.renderLayout(player)
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

}
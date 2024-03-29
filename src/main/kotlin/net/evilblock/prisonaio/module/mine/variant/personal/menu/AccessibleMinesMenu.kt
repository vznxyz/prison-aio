/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import net.evilblock.prisonaio.module.mine.variant.personal.menu.button.PrivateMineButton
import org.bukkit.entity.Player

class AccessibleMinesMenu : BrowseMinesMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "My Mines"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (privateMine in PrivateMineHandler.getAccessibleMines(player.uniqueId)) {
            buttons[buttons.size] = PrivateMineButton(this, privateMine)
        }

        return buttons
    }

}
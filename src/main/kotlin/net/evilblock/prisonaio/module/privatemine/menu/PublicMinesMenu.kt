/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import net.evilblock.prisonaio.module.privatemine.menu.button.PrivateMineButton
import org.bukkit.entity.Player

class PublicMinesMenu : BrowseMinesMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Public Mines"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (privateMine in PrivateMineHandler.getPublicMines()) {
            buttons[buttons.size] = PrivateMineButton(this, privateMine)
        }

        return buttons
    }

}
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
import org.bukkit.entity.Player

class MineEditMenu(private val mine: Mine) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Mine Editor - ${mine.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            val editorButtons = mine.getEditorButtons()

            val startAt = if (editorButtons.size > 4) {
                0
            } else {
                1
            }

            for ((index, button) in editorButtons.withIndex()) {
                buttons[startAt + (index * 2)] = button
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import org.bukkit.entity.Player

class BackpackEnchantsMenu(private val backpack: Backpack) : Menu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "Backpack Enchants"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()



        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                backpack.open(player)
            }
        }
    }

}
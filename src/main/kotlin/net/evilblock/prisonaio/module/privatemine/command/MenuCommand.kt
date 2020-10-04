/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import net.evilblock.prisonaio.module.privatemine.menu.AccessibleMinesMenu
import net.evilblock.prisonaio.module.privatemine.menu.PublicMinesMenu
import org.bukkit.entity.Player

object MenuCommand {

    @Command(names = ["privatemine", "private-mine", "pmine", "pmines"])
    @JvmStatic
    fun execute(player: Player) {
        if (PrivateMineHandler.getAccessibleMines(player.uniqueId).isNotEmpty()) {
            AccessibleMinesMenu().openMenu(player)
        } else {
            PublicMinesMenu().openMenu(player)
        }
    }

}
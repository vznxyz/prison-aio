/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.privatemine.menu.MainMenu
import org.bukkit.entity.Player

object MenuCommand {

    @Command(names = ["privatemine", "pmine"])
    @JvmStatic
    fun execute(player: Player) {
        MainMenu().openMenu(player)
    }

}
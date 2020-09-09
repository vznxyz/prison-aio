/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.menu.UserHelpMenu
import org.bukkit.entity.Player

object HelpCommand {

    @Command(
        names = ["help", "prison help"],
        description = "Helpful information about our server"
    )
    @JvmStatic
    fun execute(player: Player) {
        UserHelpMenu().openMenu(player)
    }

}
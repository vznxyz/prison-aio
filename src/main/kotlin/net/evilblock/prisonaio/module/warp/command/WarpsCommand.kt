/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.warp.menu.WarpsMenu
import org.bukkit.entity.Player

object WarpsCommand {

    @Command(
        names = ["warps"],
        description = "Opens the Warps GUI"
    )
    @JvmStatic
    fun execute(player: Player) {
        WarpsMenu().openMenu(player)
    }

}
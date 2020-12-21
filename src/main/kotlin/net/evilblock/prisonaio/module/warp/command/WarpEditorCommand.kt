/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.warp.menu.WarpEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object WarpEditorCommand {

    @Command(
        names = ["warp editor", "warps editor"],
        description = "Opens the Warp Editor GUI",
        permission = Permissions.WARPS_MANAGE
    )
    @JvmStatic
    fun execute(player: Player) {
        WarpEditorMenu().openMenu(player)
    }

}
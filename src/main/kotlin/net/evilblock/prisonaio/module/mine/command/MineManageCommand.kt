/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.menu.MineEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object MineManageCommand {

    @Command(
        names = ["mine manage", "mine admin"],
        description = "Opens a menu of tools to manage a mine",
        permission = Permissions.MINES_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        MineEditorMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.menu.MineEditMenu
import net.evilblock.prisonaio.module.mine.menu.MineEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object MineManageCommand {

    @Command(
        names = ["mine manage", "mine admin", "mine edit"],
        description = "Opens a menu of tools to manage a mine",
        permission = Permissions.MINES_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        MineEditorMenu().openMenu(player)
    }

    @Command(
        names = ["mine manage", "mine admin", "mine edit"],
        description = "Opens a menu of tools to manage a mine",
        permission = Permissions.MINES_ADMIN
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        MineEditMenu(mine).openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.rank.menu.RankEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object RankEditorCommand {

    @Command(
        names = ["prison rank editor"],
        description = "Open the rank editor",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        RankEditorMenu().openMenu(player)
    }

}
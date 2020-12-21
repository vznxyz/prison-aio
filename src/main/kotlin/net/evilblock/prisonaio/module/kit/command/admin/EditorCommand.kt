/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.kit.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.kit.menu.KitEditorMenu
import org.bukkit.entity.Player

object EditorCommand {

    @Command(
        names = ["kit editor", "kits editor"],
        description = "Open the Kit Editor",
        permission = "kits.editor"
    )
    @JvmStatic
    fun execute(player: Player) {
        KitEditorMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.crate.menu.CrateEditorMenu
import org.bukkit.entity.Player

object CrateEditorCommand {

    @Command(
        names = ["crate editor", "prison crate editor"],
        description = "Open the crate editor",
        permission = "prisonaio.crates.editor"
    )
    @JvmStatic
    fun execute(player: Player) {
        CrateEditorMenu().openMenu(player)
    }

}
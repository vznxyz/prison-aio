/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.arena.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.arena.menu.ArenaEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object EventArenaEditorCommand {

    @Command(
        names = ["event arena editor", "events arena editor"],
        description = "Opens the event arena editor",
        permission = Permissions.EVENTS_EDIT
    )
    @JvmStatic
    fun execute(player: Player) {
        ArenaEditorMenu().openMenu(player)
    }

}
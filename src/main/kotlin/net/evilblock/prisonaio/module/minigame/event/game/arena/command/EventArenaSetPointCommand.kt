/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.arena.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArenaHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EventArenaSetPointCommand {

    @Command(
        names = ["event arena set-point", "events arena set-point"],
        description = "Set the A/B point location of an arena",
        permission = Permissions.EVENTS_EDIT,
        async = true
    )
    @JvmStatic
    fun setPointSpawn(player: Player, @Param(name = "a/b") point: String, @Param(name = "arena") arena: EventGameArena) {
        if (point.equals("a", ignoreCase = true) || point.equals("b", ignoreCase = true)) {
            if (point.equals("a", ignoreCase = true)) {
                arena.pointA = player.location
            } else {
                arena.pointB = player.location
            }

            EventGameArenaHandler.saveData()

            player.sendMessage("${ChatColor.GREEN}Updated point ${point.toUpperCase()} for arena `${arena.name}`!")
        } else {
            player.sendMessage("${ChatColor.RED}Point must be either a/b!")
        }
    }

}
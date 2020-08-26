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

object EventArenaSetSpectatorCommand {

    @Command(
        names = ["event arena set-spectator", "events arena set-spectator"],
        description = "Set the spectator spawn point of an arena",
        permission = Permissions.EVENTS_EDIT,
        async = true
    )
    @JvmStatic
    fun setSpectatorSpawn(player: Player, @Param(name = "arena") arena: EventGameArena) {
        arena.spectatorLocation = player.location
        EventGameArenaHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Updated spectator location for arena `${arena.name}`!")
    }

}
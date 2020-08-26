/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ForceJoinCommand {

    @Command(
        names = ["event admin force-join", "events admin force-join"],
        description = "Forcefully make a player join the ongoing event",
        permission = Permissions.EVENTS_HOST_CONTROLS
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") target: Player) {
        if (!EventGameHandler.isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is no ongoing event!")
            return
        }

        try {
            EventGameHandler.getOngoingGame()!!.forceAddPlayer(target)
        } catch (e: Exception) {
            player.sendMessage("Failed to add player to event: ${ChatColor.RED}${e.message}")
        }
    }

}
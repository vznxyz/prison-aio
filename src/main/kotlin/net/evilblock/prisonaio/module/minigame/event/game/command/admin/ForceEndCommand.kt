/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ForceEndCommand {

    @Command(
        names = ["event admin force-end", "events admin force-end"],
        description = "Forcefully end an event",
        permission = Permissions.EVENTS_HOST_CONTROLS
    )
    @JvmStatic
    fun execute(player: Player) {
        if (!EventGameHandler.isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is no ongoing event!")
            return
        }

        EventGameHandler.getOngoingGame()!!.endGame()
        player.sendMessage("${ChatColor.GREEN}Successfully ended the ongoing event!")
    }

}
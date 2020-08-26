/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpectateCommand {

    @Command(
        names = ["event spectate", "events spectate"],
        description = "Spectate an ongoing event"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (!EventGameHandler.isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is no ongoing event!")
            return
        }

        val ongoingGame = EventGameHandler.getOngoingGame()!!
        if (ongoingGame.isPlayingOrSpectating(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You are already playing or spectating this event!")
            return
        }

        try {
            ongoingGame.addSpectator(player)
        } catch (e: IllegalStateException) {
            player.sendMessage("${ChatColor.RED}${e.message}")
        }
    }

}
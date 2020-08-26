package net.evilblock.prisonaio.module.minigame.event.game.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object LeaveCommand {

    @Command(
        names = ["leave", "event leave", "events leave"],
        description = "Leave the event"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (!EventGameHandler.isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is no ongoing event!")
            return
        }

        val ongoingGame = EventGameHandler.getOngoingGame()!!
        if (!ongoingGame.isPlaying(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You are not playing in the ongoing event!")
            return
        }

        ongoingGame.removePlayer(player)
    }

}
package net.evilblock.prisonaio.module.minigame.event.game.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object JoinCommand {

    @Command(
        names = ["join", "event join", "events join"],
        description = "Join an ongoing event"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (!EventGameHandler.isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is no ongoing event!")
            return
        }

        val ongoingGame = EventGameHandler.getOngoingGame()!!
        if (ongoingGame.isPlayingOrSpectating(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You are already playing this event!")
            return
        }

        try {
            ongoingGame.addPlayer(player)
        } catch (e: IllegalStateException) {
            player.sendMessage("${ChatColor.RED}${e.message}")
        }
    }

}
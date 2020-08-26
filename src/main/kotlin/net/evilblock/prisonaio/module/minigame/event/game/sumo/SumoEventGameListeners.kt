package net.evilblock.prisonaio.module.minigame.event.game.sumo

import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object SumoEventGameListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame() is SumoEventGame) {
            val ongoingGame = EventGameHandler.getOngoingGame() as SumoEventGame
            if (ongoingGame.isCurrentlyFighting(event.player)) {
                if (System.currentTimeMillis() < ongoingGame.startedAt!! + 6000L) {
                    event.isCancelled = true
                    event.player.teleport(event.from)
                    return
                }

                if (event.player.location.y <= ongoingGame.getDeathHeight()) {
                    ongoingGame.eliminatePlayer(event.player, ongoingGame.getOpponent(event.player))
                }
            }
        }
    }

}
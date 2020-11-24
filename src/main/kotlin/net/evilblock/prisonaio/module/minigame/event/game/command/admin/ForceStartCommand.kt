package net.evilblock.prisonaio.module.minigame.event.game.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameState
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ForceStartCommand {

    @Command(
        names = ["event admin force-start", "events admin force-start"],
        description = "Forcefully start an event",
        permission = Permissions.EVENTS_HOST_CONTROLS
    )
    @JvmStatic
    fun execute(player: Player) {
        if (!EventGameHandler.isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is no ongoing event!")
            return
        }

        val ongoingGame = EventGameHandler.getOngoingGame()!!
        if (ongoingGame.state === EventGameState.WAITING) {
            ongoingGame.forceStart()
        } else {
            player.sendMessage("${ChatColor.RED}You can't force start an event that has already been started!")
        }
    }

}
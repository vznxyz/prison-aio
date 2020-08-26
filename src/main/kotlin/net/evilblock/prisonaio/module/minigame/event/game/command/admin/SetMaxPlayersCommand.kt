package net.evilblock.prisonaio.module.minigame.event.game.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler.getOngoingGame
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SetMaxPlayersCommand {

    @Command(
        names = ["event set-max-players", "events set-max-players"],
        description = "Set the ongoing game's max players",
        permission = Permissions.EVENTS_EDIT,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "players") maxPlayers: Int) {
        val game = getOngoingGame()
        if (game != null) {
            game.maxPlayers = maxPlayers
            player.sendMessage("${ChatColor.GREEN}Set max players of ${game.gameType.displayName} ${ChatColor.GREEN}event to ${ChatColor.WHITE}$maxPlayers${ChatColor.GREEN}!")
        } else {
            player.sendMessage("${ChatColor.RED}There is no ongoing game!")
        }
    }

}
package net.evilblock.prisonaio.module.minigame.event.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.config.EventConfig
import net.evilblock.prisonaio.module.minigame.event.config.EventConfigHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SetLobbyCommand {

    @Command(
        names = ["event set-lobby", "events set-lobby"],
        description = "Sets the lobby location for events",
        permission = Permissions.EVENTS_EDIT,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        EventConfigHandler.config.lobbyLocation = player.location
        EventConfigHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Updated the events lobby location!")
    }

}
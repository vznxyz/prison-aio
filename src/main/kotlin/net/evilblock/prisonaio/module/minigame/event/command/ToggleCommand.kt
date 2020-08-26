package net.evilblock.prisonaio.module.minigame.event.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ToggleCommand {

    @Command(
        names = ["event toggle", "events toggle"],
        description = "Toggle if events can be hosted",
        permission = Permissions.EVENTS_TOGGLE,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        EventGameHandler.disabled = !EventGameHandler.disabled

        if (EventGameHandler.disabled) {
            sender.sendMessage("${ChatColor.RED}Events are now disabled!")
        } else {
            sender.sendMessage("${ChatColor.GREEN}Events are now enabled!")
        }
    }

}
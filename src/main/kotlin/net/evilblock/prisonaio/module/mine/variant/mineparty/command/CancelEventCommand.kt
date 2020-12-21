package net.evilblock.prisonaio.module.mine.variant.mineparty.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CancelEventCommand {

    @Command(
        names = ["mineparty cancel", "mine-party cancel"],
        description = "Cancel a MineParty event",
        permission = Permissions.MINE_PARTY,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (!MinePartyHandler.isEventActive()) {
            sender.sendMessage("${ChatColor.RED}There is no active event!")
            return
        }

        MinePartyHandler.cancelEvent()
    }

}
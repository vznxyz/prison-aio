package net.evilblock.prisonaio.module.mine.variant.mineparty.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object SetProgressCommand {

    @Command(
        names = ["mineparty set-progress", "mine-party set-progress"],
        description = "Sets the progress of a MineParty event",
        permission = Permissions.MINE_PARTY
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "progress") progress: Int) {
        if (!MinePartyHandler.isEventActive()) {
            sender.sendMessage("${ChatColor.RED}There is no active event!")
            return
        }

        MinePartyHandler.getEvent()!!.progress = progress

        sender.sendMessage("${ChatColor.GREEN}Set progress to $progress!")
    }

}
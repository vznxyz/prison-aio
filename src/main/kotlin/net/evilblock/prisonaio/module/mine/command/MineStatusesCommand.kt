package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object MineStatusesCommand {

    @Command(
        names = ["mine statuses"],
        description = "",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        for (mine in MineHandler.getMines()) {
            if (mine.region != null) {
                sender.sendMessage("${ChatColor.GRAY}Mine ${ChatColor.RED}${ChatColor.BOLD}${mine.id} ${ChatColor.GRAY}has ${ChatColor.RED}${mine.getRemainingPercentage()}% ${ChatColor.GRAY}of its blocks remaining.")
            }
        }
    }

}
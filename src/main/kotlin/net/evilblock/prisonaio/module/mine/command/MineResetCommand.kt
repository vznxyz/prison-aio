package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineResetCommand {

    @Command(
        names = ["mine reset"],
        description = "Open the mine editor",
        permission = "prisonaio.mines.reset",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        if (mine.getBreakableRegion() != null) {
            mine.resetRegion()
            player.sendMessage("${ChatColor.GREEN}You successfully reset the ${mine.id} mine.")
        } else {
            player.sendMessage("${ChatColor.RED}That mine doesn't have a region set.")
        }
    }

}
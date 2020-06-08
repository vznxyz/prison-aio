package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineDeleteCommand {

    @Command(
        names = ["mine delete"],
        description = "Delete an existing mine",
        permission = "prisonaio.mines.delete"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        MineHandler.deleteMine(mine)

        player.sendMessage("${ChatColor.GREEN}Deleted mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}.")
    }

}
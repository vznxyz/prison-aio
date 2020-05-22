package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineCreateCommand {

    @Command(names = ["mine create"], description = "Create a new mine", permission = "prisonaio.mines.create")
    @JvmStatic
    fun execute(player: Player, @Param(name = "name") name: String) {
        if (MineHandler.getMineById(name).isPresent) {
            player.sendMessage("${ChatColor.RED}Mines must have a unique name and `${ChatColor.WHITE}$name${ChatColor.RED}` is already taken.")
            return
        }

        MineHandler.createMine(name)
        MineHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Created new mine ${ChatColor.WHITE}$name${ChatColor.GREEN}.")
    }

}
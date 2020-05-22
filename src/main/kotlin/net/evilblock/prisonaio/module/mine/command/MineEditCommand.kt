package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.menu.MineEditMenu
import org.bukkit.entity.Player

object MineEditCommand {

    @Command(names = ["mine edit"], description = "Open the mine editor", permission = "prisonaio.mines.edit")
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        MineEditMenu(mine).openMenu(player)
    }

}
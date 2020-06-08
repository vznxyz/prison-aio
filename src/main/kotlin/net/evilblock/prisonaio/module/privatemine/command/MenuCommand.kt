package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.privatemine.menu.MainMenu
import org.bukkit.entity.Player

object MenuCommand {

    @Command(names = ["privatemine", "pmine"])
    @JvmStatic
    fun execute(player: Player) {
        MainMenu().openMenu(player)
    }

}
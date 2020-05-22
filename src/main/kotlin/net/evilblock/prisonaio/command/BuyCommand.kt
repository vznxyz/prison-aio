package net.evilblock.prisonaio.command

import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player
import org.quadrex.buycraft.menu.MainMenu

object BuyCommand {

    @Command(
        names = ["buy"],
        description = "View the Buycraft store"
    )
    @JvmStatic
    fun execute(player: Player) {
        MainMenu(player).open()
    }

}
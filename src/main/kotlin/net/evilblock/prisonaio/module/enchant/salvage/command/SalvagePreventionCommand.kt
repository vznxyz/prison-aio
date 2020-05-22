package net.evilblock.prisonaio.module.enchant.salvage.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.enchant.salvage.menu.SalvagePreventionMenu
import org.bukkit.entity.Player

object SalvagePreventionCommand {

    @Command(
        names = ["prison salvage-prevention"],
        description = "Open the Salvage Prevention Manager",
        permission = "prisonaio.salvage.admin"
    )
    @JvmStatic
    fun execute(player: Player) {
        SalvagePreventionMenu().openMenu(player)
    }

}
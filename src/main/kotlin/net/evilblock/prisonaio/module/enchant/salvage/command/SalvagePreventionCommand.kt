/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
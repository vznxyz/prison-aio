/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.salvage.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.tool.pickaxe.salvage.menu.SalvagePreventionEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object SalvagePreventionEditorCommand {

    @Command(
        names = ["prison salvage-prevention"],
        description = "Open the Salvage Prevention editor",
        permission = Permissions.SALVAGE_PREVENTION_EDITOR
    )
    @JvmStatic
    fun execute(player: Player) {
        SalvagePreventionEditorMenu().openMenu(player)
    }

}
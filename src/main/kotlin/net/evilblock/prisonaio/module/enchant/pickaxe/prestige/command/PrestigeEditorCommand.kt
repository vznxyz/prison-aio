/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.pickaxe.prestige.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.enchant.pickaxe.prestige.menu.PrestigeEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object PrestigeEditorCommand {

    @Command(
        names = ["prison pickaxe-prestige"],
        description = "Opens the Pickaxe Prestige editor",
        permission = Permissions.PICKAXE_PRESTIGE_EDITOR
    )
    @JvmStatic
    fun execute(player: Player) {
        PrestigeEditorMenu().openMenu(player)
    }

}
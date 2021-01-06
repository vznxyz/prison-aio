/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.rules.menu.RulesEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object GangRulesEditorCommand {

    @Command(
        names = ["gangs admin rules editor"],
        description = "Opens the Gang Rules Editor",
        permission = Permissions.GANGS_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        RulesEditorMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.admin.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.admin.menu.PrisonManagementMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object ManageCommand {

    @Command(
        names = ["prison manage", "prison admin"],
        description = "Opens the Prison Management menu",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        PrisonManagementMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.enchant.menu.admin.ManageEnchantsMenu
import org.bukkit.entity.Player

object ManageEnchantsCommand {

    @Command(
        names = ["prison enchants"],
        description = "Manage enchant variables",
        permission = "prisonaio.enchants.manage"
    )
    @JvmStatic
    fun execute(player: Player) {
        ManageEnchantsMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.menu.BackpackMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object BackpackViewCommand {

    @Command(
        names = ["backpack view"],
        description = "View a backpack's contents",
        permission = "backpack.view"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "id") backpack: Backpack) {
        BackpackMenu(backpack).openMenu(player)
        player.sendMessage("${ChatColor.GOLD}Opening backpack with ID ${backpack.id}...")
    }

}
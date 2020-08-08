/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.perk.menu.GrantedPerksMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object PerksGrantsCommand {

    @Command(
        names = ["user perks grants"],
        description = "View a user's perk grants",
        permission = "prisonaio.user.perks.grants",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player", defaultValue = "self") target: User) {
        GrantedPerksMenu(target).openMenu(player)
    }

}
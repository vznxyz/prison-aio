/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.UserRankupsMenu
import org.bukkit.entity.Player

object RankupsCommand {

    @Command(names = ["ranks", "rankups"], description = "Shows each rankup and information about them")
    @JvmStatic
    fun execute(player: Player) {
        UserRankupsMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
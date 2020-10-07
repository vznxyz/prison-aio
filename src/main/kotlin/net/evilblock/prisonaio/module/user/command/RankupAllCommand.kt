/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player

object RankupAllCommand {

    @Command(
        names = ["rankup all", "rankupall", "rankupmax", "maxrankup"],
        description = "Rankup as many levels as you can"
    )
    @JvmStatic
    fun execute(player: Player) {
        UserHandler.getUser(player.uniqueId).purchaseMaxRankups(player, manual = true)
    }

}
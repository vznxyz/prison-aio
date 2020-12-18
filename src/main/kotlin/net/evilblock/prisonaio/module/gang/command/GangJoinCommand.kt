/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.entity.Player

object GangJoinCommand {

    @Command(
        names = ["gang join", "gangs join", "gang accept", "gangs accept"],
        description = "Join a gang you've been invited to",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "gang") gang: Gang) {
        GangHandler.attemptJoinGang(player, gang)
    }

}
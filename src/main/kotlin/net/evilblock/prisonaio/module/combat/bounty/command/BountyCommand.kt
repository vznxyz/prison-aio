/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.bounty.command

import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player

object BountyCommand {

    @Command(
        names = ["bounty"],
        description = "Place a bounty on a player's head"
    )
    @JvmStatic
    fun execute(player: Player) {

    }

}
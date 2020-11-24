/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.lottery.command

import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player

object LotteryCommand {

    @Command(
        names = ["lottery"],
        description = "Opens the Lottery menu"
    )
    @JvmStatic
    fun execute(player: Player) {

    }

}
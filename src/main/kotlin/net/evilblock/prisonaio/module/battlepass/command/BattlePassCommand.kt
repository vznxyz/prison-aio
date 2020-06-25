/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassMenu
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player

object BattlePassCommand {

    @Command(
        names = ["battlepass", "bp", "junkiepass", "jp"],
        description = "Open the JunkiePass"
    )
    @JvmStatic
    fun execute(player: Player) {
        BattlePassMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
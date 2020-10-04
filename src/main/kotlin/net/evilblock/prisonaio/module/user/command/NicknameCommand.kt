/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.UserNicknameMenu
import org.bukkit.entity.Player

object NicknameCommand {

    @Command(
        names = ["nickname", "nick", "nn", "nametag", "colors", "color"],
        description = "Change the appearance of your name"
    )
    @JvmStatic
    fun execute(player: Player) {
        UserNicknameMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
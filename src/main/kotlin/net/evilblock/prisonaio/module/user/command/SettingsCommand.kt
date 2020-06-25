/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.menu.UserSettingsMenu
import org.bukkit.entity.Player

object SettingsCommand {

    @Command(
        names = ["settings", "options"],
        description = "Manage your account settings"
    )
    @JvmStatic
    fun execute(player: Player) {
        UserSettingsMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
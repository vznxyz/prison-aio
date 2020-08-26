/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.news.menu.NewsEditor
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object NewsEditorCommand {

    @Command(
        names = ["prison news editor"],
        description = "Open the news editor",
        permission = Permissions.NEWS_EDITOR
    )
    @JvmStatic
    fun execute(player: Player) {
        NewsEditor().openMenu(player)
    }

}
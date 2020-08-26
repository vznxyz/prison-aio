/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.news.menu.NewsMenu
import org.bukkit.entity.Player

object NewsCommand {

    @Command(
        names = ["news", "changelog", "devlog", "announcements"],
        description = "View server updates/news"
    )
    @JvmStatic
    fun execute(player: Player) {
        NewsMenu().openMenu(player)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.user.news.News
import net.evilblock.prisonaio.module.user.news.NewsHandler
import org.bukkit.entity.Player

class EditNewsTextMenu(private val news: News) : TextEditorMenu(lines = news.lines) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit News Text"
    }

    override fun onSave(player: Player, list: List<String>) {
        Tasks.async {
            news.lines = ArrayList(list)
            NewsHandler.saveData()
        }
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditNewsMenu(news).openMenu(player)
        }
    }

}
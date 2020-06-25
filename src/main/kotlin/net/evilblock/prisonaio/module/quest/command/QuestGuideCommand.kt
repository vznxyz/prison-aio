/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.quest.menu.QuestGuideMenu
import org.bukkit.entity.Player

object QuestGuideCommand {

    @Command(names = ["quest guide", "quests guide", "quest help", "quests help"], description = "Information that can guide you through your quests")
    @JvmStatic
    fun execute(player: Player) {
        QuestGuideMenu().openMenu(player)
    }

}
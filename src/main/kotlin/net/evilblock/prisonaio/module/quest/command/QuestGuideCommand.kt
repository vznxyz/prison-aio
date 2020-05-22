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
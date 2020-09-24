/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.mission

import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener

object TutorialMission : QuestMission, Listener {

    override fun getQuest(): Quest {
        return TutorialQuest
    }

    override fun getId(): String {
        return "home-sweet-home"
    }

    override fun getName(): String {
        return "Home Sweet Home"
    }

    override fun getOrder(): Int {
        return 1
    }

    override fun getMissionText(player: Player): String {
        return "You've landed yourself in prison. This is your new home. Meet ${ChatColor.stripColor(QuestsModule.getNpcName("tutorial-guide"))}. Follow and listen to what he has to say. He will teach you everything you need to know."
    }

}
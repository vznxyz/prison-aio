/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial

import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import net.evilblock.prisonaio.module.quest.progress.QuestProgress

object TutorialQuest : Quest {

    override fun getId(): String {
        return "tutorial"
    }

    override fun getName(): String {
        return "Tutorial"
    }

    override fun getSortedMissions(): List<QuestMission> {
        return listOf()
    }

    override fun startProgress(): QuestProgress {

    }

}
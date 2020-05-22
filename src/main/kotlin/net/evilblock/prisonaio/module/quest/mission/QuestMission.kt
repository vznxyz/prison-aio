package net.evilblock.prisonaio.module.quest.mission

import net.evilblock.prisonaio.module.quest.Quest
import org.bukkit.entity.Player

interface QuestMission<T : Quest<T>> {

    fun getQuest(): T

    fun getId(): String

    fun getName(): String

    fun getMissionText(player: Player): String

    fun getOrder(): Int

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
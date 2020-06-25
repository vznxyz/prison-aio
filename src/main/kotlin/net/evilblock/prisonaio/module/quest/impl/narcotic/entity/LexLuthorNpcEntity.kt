/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.narcotic.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import org.bukkit.ChatColor
import org.bukkit.Location

class LexLuthorNpcEntity(location: Location) : NpcEntity(lines = listOf(""), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateLines(lines = arrayListOf(
            "${ChatColor.YELLOW}${ChatColor.BOLD}Lex Luthor",
            "${ChatColor.GRAY}(Start Quest: ${NarcoticsQuest.getName()})"
        ))
    }

}
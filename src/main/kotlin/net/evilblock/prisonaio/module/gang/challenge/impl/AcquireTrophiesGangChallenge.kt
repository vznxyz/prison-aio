/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge.impl

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.challenge.GangChallenge
import org.bukkit.ChatColor

class AcquireTrophiesGangChallenge(id: String, reward: Int, val amount: Int) : GangChallenge(id, reward) {

    override fun getRenderedName(): String {
        return "Acquire ${NumberUtils.format(amount)} Trophies"
    }

    override fun renderGoal(): List<String> {
        return listOf(
            "${ChatColor.GRAY}You and your gang members need to",
            "${ChatColor.GRAY}acquire ${NumberUtils.format(amount)} trophies."
        )
    }

    override fun renderProgress(gang: Gang): String {
        val percentage = ProgressBarBuilder.percentage(gang.getTrophies(), amount)
        return ProgressBarBuilder(char = '⬛').build(percentage)
    }

    override fun meetsCompletionRequirements(gang: Gang): Boolean {
        return gang.hasTrophies(amount)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge.impl

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.challenge.GangChallenge
import org.bukkit.ChatColor

class AcquirePrestigesGangChallenge(id: String, reward: Int, private val amount: Int) : GangChallenge(id, reward) {

    override fun getRenderedName(): String {
        return "Prestige ${NumberUtils.format(amount)} Times"
    }

    override fun renderGoal(): List<String> {
        return listOf(
            "${ChatColor.GRAY}You and your gang members need to",
            "${ChatColor.GRAY}reach your next prestige ${NumberUtils.format(amount)} times."
        )
    }

    override fun renderProgress(gang: Gang): List<String> {
        val percentage = ProgressBarBuilder.percentage(gang.challengesData.acquiredPrestiges.toInt(), amount)
        val progressColor = ProgressBarBuilder.colorPercentage(percentage)
        val progressBar = ProgressBarBuilder(char = '⬛').build(percentage)
        return listOf("${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ($progressColor${percentage.toInt()}%${ChatColor.GRAY})")
    }

    override fun meetsCompletionRequirements(gang: Gang): Boolean {
        return gang.challengesData.acquiredPrestiges >= amount
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.slot

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.scoreboard.PrisonScoreGetter
import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardSlot
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RankupSlot : ScoreboardSlot() {

    override fun priority(): Int {
        return 1
    }

    override fun render(player: Player, user: User): List<String> {
        return arrayListOf<String>().also { lines ->
            val nextRank = RankHandler.getNextRank(user.getRank())
            if (nextRank != null) {
                val nextRankPrice = nextRank.getPrice(user.getPrestige())
                val formattedPrice = NumberUtils.format(nextRankPrice)

                val progressPercentage = if (user.hasMoneyBalance(nextRankPrice)) {
                    100.0
                } else {
                    (user.getMoneyBalance().toDouble() / nextRankPrice) * 100.0
                }

                val progressColor = ProgressBarBuilder.colorPercentage(progressPercentage)
                val progressBar = ProgressBarBuilder(char = '⬛').build(progressPercentage)

                lines.add("  ${PrisonScoreGetter.PRIMARY_COLOR}${ChatColor.BOLD}Rankup")
                lines.add("  ${ChatColor.GRAY}${user.getRank().displayName} ${ChatColor.GRAY}-> ${nextRank.displayName} ${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedPrice")
                lines.add("  ${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} $progressColor${progressPercentage.toInt()}%")
            }
        }
    }

    override fun canRender(player: Player, user: User): Boolean {
        return true
    }

}
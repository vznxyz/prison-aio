/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.slot

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardSlot
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MinePartyPlayingSlot : ScoreboardSlot() {

    override fun priority(): Int {
        return 10
    }

    override fun render(player: Player, user: User): List<String> {
        val mineParty = MinePartyHandler.getEvent()!!

        val formattedTime = TimeUtil.formatIntoAbbreviatedString(mineParty.getRemainingSeconds())
        val formattedProgress = NumberUtils.format(mineParty.progress)
        val formattedGoal = NumberUtils.format(mineParty.goal)

        val progressPercentage = NumberUtils.percentage(mineParty.progress, mineParty.goal)
        val progressBar = ProgressBarBuilder().build(progressPercentage)

        return arrayListOf<String>().also { lines ->
            lines.add("  ${MinePartyHandler.SIMPLE_NAME}")
            lines.add("  ${ChatColor.GRAY}Time: ${ChatColor.AQUA}$formattedTime")
            lines.add("  ${ChatColor.GRAY}Goal: ${ChatColor.AQUA}$formattedGoal")
            lines.add("  ${ChatColor.GRAY}Progress: ${ChatColor.AQUA}$formattedProgress")
            lines.add("  ${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}${progressBar}${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${ChatColor.AQUA}${NumberUtils.formatDecimal(progressPercentage)}%")
        }
    }

    override fun canRender(player: Player, user: User): Boolean {
        val event = MinePartyHandler.getEvent()
        return event != null && event.active && !event.isExpired() && event.isNearbyMine(player)
    }

}
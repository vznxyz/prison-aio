/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.scoreboard

import net.evilblock.cubed.scoreboard.ScoreGetter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldownHandler
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import net.evilblock.prisonaio.module.combat.region.CombatRegion
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object PrisonScoreGetter : ScoreGetter {

    // https://www.fileformat.info/info/unicode/block/box_drawing/list.htm
    // ╔═══════╗  ┌───────┐
    // ║       ║  │       │
    // ╚═══════╝  └───────┘

    override fun getScores(scores: LinkedList<String>, player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (!user.getSettingOption(UserSetting.SCOREBOARD_VISIBILITY).getValue<Boolean>()) {
            return
        }

        val region = RegionsModule.findRegion(player.location)
        if (region is CombatRegion) {
            scores.add("")
            scores.add("  ${ChatColor.YELLOW}${ChatColor.BOLD}Kills: ${ChatColor.RED}${user.statistics.getKills()}")
            scores.add("  ${ChatColor.YELLOW}${ChatColor.BOLD}Deaths: ${ChatColor.RED}${user.statistics.getDeaths()}")

            val combatTimer = CombatTimerHandler.getTimer(player.uniqueId)
            if (combatTimer != null && !combatTimer.hasExpired()) {
                scores.add("  ${ChatColor.RED}${ChatColor.BOLD}Combat: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(combatTimer.getRemainingSeconds().toInt())}")
            }

            val enderpearlCooldown = EnderpearlCooldownHandler.getCooldown(player.uniqueId)
            if (enderpearlCooldown != null && !enderpearlCooldown.hasExpired()) {
                scores.add("  ${ChatColor.YELLOW}${ChatColor.BOLD}Enderpearl: ${ChatColor.RED}${enderpearlCooldown.getRemainingSeconds()}s")
            }

            val godAppleCooldown = GodAppleCooldownHandler.getCooldown(player.uniqueId)
            if (godAppleCooldown != null && !godAppleCooldown.hasExpired()) {
                scores.add("  ${ChatColor.GOLD}${ChatColor.BOLD}Gopple: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(godAppleCooldown.getRemainingSeconds().toInt())}")
            }

            scores.add("")
            scores.add(0, "${ChatColor.DARK_GRAY}┌──────────────┐")
            scores.add("     ${ChatColor.RED}${ChatColor.ITALIC}store.minejunkie.com")
            scores.add("${ChatColor.DARK_GRAY}└──────────────┘")

            return
        }

        scores.add("")
        scores.add("  ${ChatColor.RED}${ChatColor.BOLD}${player.name}")
        scores.add("  ${ChatColor.RED}${Constants.CROSSED_SWORDS_SYMBOL} ${ChatColor.GRAY}Rank ${user.getRank().displayName}")

        if (user.getPrestige() == 0) {
            scores.add("  ${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Not Prestiged")
        } else {
            scores.add("  ${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Prestige ${user.getPrestige()}")
        }

        val moneyBalance = user.getMoneyBalance()
        val formattedMoneyBalance = NumberUtils.format(moneyBalance)
        scores.add("  ${ChatColor.RED}${Constants.MONEY_SYMBOL} ${ChatColor.GRAY}$formattedMoneyBalance")

        val formattedTokensBalance = NumberUtils.format(user.getTokensBalance())
        scores.add("  ${ChatColor.RED}${Constants.TOKENS_SYMBOL} ${ChatColor.GRAY}$formattedTokensBalance")

        scores.add("")

        val optionalNextRank = RankHandler.getNextRank(user.getRank())
        if (optionalNextRank.isPresent) {
            val nextRank = optionalNextRank.get()
            val nextRankPrice = nextRank.getPrice(user.getPrestige())
            val formattedPrice = NumberUtils.format(nextRankPrice)

            val progressPercentage = if (moneyBalance > nextRankPrice) {
                100.0
            } else {
                (moneyBalance / nextRankPrice) * 100.0
            }

            val progressColor = ProgressBarBuilder.colorPercentage(progressPercentage)
            val progressBar = ProgressBarBuilder(char = '⬛').build(progressPercentage)

            scores.add("  ${ChatColor.RED}${ChatColor.BOLD}Progress")
            scores.add("  ${ChatColor.GRAY}${user.getRank().displayName} ${ChatColor.GRAY}-> ${nextRank.displayName} ${ChatColor.GRAY}(${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedPrice${ChatColor.GRAY})")
            scores.add("  ${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ($progressColor${progressPercentage.toInt()}%${ChatColor.GRAY})")
            scores.add("")
        }

        scores.add(0, "${ChatColor.DARK_GRAY}┌──────────────┐")
        scores.add("     ${ChatColor.RED}${ChatColor.ITALIC}store.minejunkie.com")
        scores.add("${ChatColor.DARK_GRAY}└──────────────┘")
    }

}
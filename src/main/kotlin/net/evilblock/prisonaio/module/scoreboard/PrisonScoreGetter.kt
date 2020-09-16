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
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.module.scoreboard.animation.RainbowAnimation
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.ScoreboardStyleOption
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
        if (!user.settings.getSettingOption(UserSetting.SCOREBOARD_VISIBILITY).getValue<Boolean>()) {
            return
        }

        val primaryColor: ChatColor = if (user.settings.getSettingOption(UserSetting.RAINBOW_SCOREBOARD).getValue()) {
            RainbowAnimation.getCurrentDisplay()
        } else {
            ChatColor.RED
        }

        if (EventGameHandler.isOngoingGame()) {
            val game = EventGameHandler.getOngoingGame()
            if (game != null) {
                if (game.isPlayingOrSpectating(player.uniqueId)) {
                    game.getScoreboardLines(player, scores)
                    renderBorders(user, scores, primaryColor)
                    return
                }
            }
        }

        val region = RegionHandler.findRegion(player.location)
        if (region is BitmaskRegion && region.hasBitmask(RegionBitmask.DANGER_ZONE)) {
            scores.add("  $primaryColor${ChatColor.BOLD}Kills: ${ChatColor.RED}${user.statistics.getKills()}")
            scores.add("  $primaryColor${ChatColor.BOLD}Deaths: ${ChatColor.RED}${user.statistics.getDeaths()}")

            val combatTimer = CombatTimerHandler.getTimer(player.uniqueId)
            if (combatTimer != null && !combatTimer.hasExpired()) {
                scores.add("  $primaryColor${ChatColor.BOLD}Combat: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(combatTimer.getRemainingSeconds().toInt())}")
            }

            val enderpearlCooldown = EnderpearlCooldownHandler.getCooldown(player.uniqueId)
            if (enderpearlCooldown != null && !enderpearlCooldown.hasExpired()) {
                scores.add("  ${ChatColor.YELLOW}${ChatColor.BOLD}Enderpearl: ${ChatColor.RED}${enderpearlCooldown.getRemainingSeconds()}s")
            }

            val godAppleCooldown = GodAppleCooldownHandler.getCooldown(player.uniqueId)
            if (godAppleCooldown != null && !godAppleCooldown.hasExpired()) {
                scores.add("  ${ChatColor.GOLD}${ChatColor.BOLD}Gopple: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(godAppleCooldown.getRemainingSeconds().toInt())}")
            }

            renderBorders(user, scores, primaryColor)
            return
        }

        scores.add("  $primaryColor${ChatColor.BOLD}${player.name}")
        scores.add("  $primaryColor${Constants.CROSSED_SWORDS_SYMBOL} ${ChatColor.GRAY}Rank ${user.getRank().displayName}")

        if (user.getPrestige() == 0) {
            scores.add("  $primaryColor${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Not Prestiged")
        } else {
            scores.add("  $primaryColor${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Prestige ${user.getPrestige()}")
        }

        val moneyBalance = try {
            user.getMoneyBalance()
        } catch (e: Exception) {
            UserHandler.MINIMUM_MONEY_BALANCE
        }

        val formattedMoneyBalance = NumberUtils.format(moneyBalance)
        scores.add("  $primaryColor${Constants.MONEY_SYMBOL} ${ChatColor.GRAY}$formattedMoneyBalance")

        val formattedTokensBalance = NumberUtils.format(user.getTokenBalance())
        scores.add("  $primaryColor${Constants.TOKENS_SYMBOL} ${ChatColor.GRAY}$formattedTokensBalance")

        scores.add("")

        val optionalNextRank = RankHandler.getNextRank(user.getRank())
        if (optionalNextRank.isPresent) {
            val nextRank = optionalNextRank.get()
            val nextRankPrice = nextRank.getPrice(user.getPrestige())
            val formattedPrice = NumberUtils.format(nextRankPrice)

            val progressPercentage = if (user.hasMoneyBalance(nextRankPrice)) {
                100.0
            } else {
                (moneyBalance.toDouble() / nextRankPrice) * 100.0
            }

            val progressColor = ProgressBarBuilder.colorPercentage(progressPercentage)
            val progressBar = ProgressBarBuilder(char = '⬛').build(progressPercentage)

            scores.add("  $primaryColor${ChatColor.BOLD}Progress")
            scores.add("  ${ChatColor.GRAY}${user.getRank().displayName} ${ChatColor.GRAY}-> ${nextRank.displayName} ${ChatColor.GRAY}(${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedPrice${ChatColor.GRAY})")
            scores.add("  ${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ($progressColor${progressPercentage.toInt()}%${ChatColor.GRAY})")
        }

        renderBorders(user, scores, primaryColor)
    }

    private fun renderBorders(user: User, list: MutableList<String>, primaryColor: ChatColor) {
        when (user.settings.getSettingOption(UserSetting.SCOREBOARD_STYLE).getValue<ScoreboardStyleOption.ScoreboardStyle>()) {
            ScoreboardStyleOption.ScoreboardStyle.SIMPLE -> {
                list.add(0, "${ChatColor.BLUE}${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}----------------------")
                list.add("")
                list.add("      $primaryColor${ChatColor.ITALIC}play.minejunkie.com")
                list.add("${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}----------------------")
            }
            ScoreboardStyleOption.ScoreboardStyle.FANCY -> {
                list.add(0, "${ChatColor.DARK_GRAY}┌──────────────┐")
                list.add(1, "")
                list.add("")
                list.add("      $primaryColor${ChatColor.ITALIC}play.minejunkie.com")
                list.add("${ChatColor.DARK_GRAY}└──────────────┘")
            }
        }
    }

}
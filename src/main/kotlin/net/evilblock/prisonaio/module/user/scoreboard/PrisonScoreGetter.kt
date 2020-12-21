/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard

import net.evilblock.cubed.scoreboard.ScoreGetter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Rainbow
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldownHandler
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.scoreboard.animation.RainbowAnimation
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
        if (!user.settings.getSettingOption(UserSetting.SCOREBOARD_VISIBILITY).getValue<Boolean>()) {
            return
        }

        val primaryColor: ChatColor = if (user.settings.getSettingOption(UserSetting.RAINBOW_SCOREBOARD).getValue()) {
            RainbowAnimation.getCurrentDisplay()
        } else {
            ChatColor.RED
        }

        val padding = "  "

        if (EventGameHandler.isOngoingGame()) {
            val game = EventGameHandler.getOngoingGame()
            if (game != null) {
                if (game.isPlayingOrSpectating(player.uniqueId)) {
                    game.getScoreboardLines(player, scores)
                    renderBorders(user, scores)
                    return
                }
            }
        }

        scores.add("${padding}$primaryColor${ChatColor.BOLD}${player.name}")

        val region = RegionHandler.findRegion(player.location)
        if (region is BitmaskRegion && region.hasBitmask(RegionBitmask.DANGER_ZONE)) {
            scores.add("${padding}$primaryColor${ChatColor.BOLD}Kills: ${ChatColor.RED}${user.statistics.getKills()}")
            scores.add("${padding}$primaryColor${ChatColor.BOLD}Deaths: ${ChatColor.RED}${user.statistics.getDeaths()}")

            val combatTimer = CombatTimerHandler.getTimer(player.uniqueId)
            if (combatTimer != null && !combatTimer.hasExpired()) {
                scores.add("$padding  $primaryColor${ChatColor.BOLD}Combat: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(combatTimer.getRemainingSeconds().toInt())}")
            }

            val enderpearlCooldown = EnderpearlCooldownHandler.getCooldown(player.uniqueId)
            if (enderpearlCooldown != null && !enderpearlCooldown.hasExpired()) {
                scores.add("${padding}${ChatColor.YELLOW}${ChatColor.BOLD}Enderpearl: ${ChatColor.RED}${enderpearlCooldown.getRemainingSeconds()}s")
            }

            val godAppleCooldown = GodAppleCooldownHandler.getCooldown(player.uniqueId)
            if (godAppleCooldown != null && !godAppleCooldown.hasExpired()) {
                scores.add("${padding}${ChatColor.GOLD}${ChatColor.BOLD}Gopple: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(godAppleCooldown.getRemainingSeconds().toInt())}")
            }

            renderBorders(user, scores)
            return
        }

        if (user.getPrestige() == 0) {
            scores.add("${padding}${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}No Prestige")
        } else {
            scores.add("${padding}${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Prestige ${user.getPrestige()}")
        }

        val moneyBalance = try {
            user.getMoneyBalance()
        } catch (e: Exception) {
            UserHandler.MINIMUM_MONEY_BALANCE
        }

        val formattedMoneyBalance = NumberUtils.format(moneyBalance)
        scores.add("${padding}${ChatColor.GREEN}${Constants.MONEY_SYMBOL} ${ChatColor.GRAY}$formattedMoneyBalance")

        val formattedTokensBalance = NumberUtils.format(user.getTokenBalance())
        scores.add("${padding}${ChatColor.YELLOW}${Constants.TOKENS_SYMBOL} ${ChatColor.GRAY}$formattedTokensBalance")

        scores.add("")

        val minePartyEvent = MinePartyHandler.getEvent()
        if (minePartyEvent != null) {
            val color = Rainbow.currentColor

            val formattedTime = TimeUtil.formatIntoAbbreviatedString(minePartyEvent.getRemainingSeconds())
            val formattedProgress = NumberUtils.format(minePartyEvent.progress)
            val formattedGoal = NumberUtils.format(minePartyEvent.goal)

            val progressPercentage = NumberUtils.percentage(minePartyEvent.progress, minePartyEvent.goal)
            val progressBar = ProgressBarBuilder().build(progressPercentage)

            scores.add("${padding}${color}${ChatColor.BOLD}MineParty ${ChatColor.GRAY}(/mineparty)")
            scores.add("${ChatColor.GRAY}Time: ${color}${ChatColor.BOLD}$formattedTime")
            scores.add("${ChatColor.GRAY}Progress: ${color}${ChatColor.BOLD}${formattedProgress}${ChatColor.GRAY}/${formattedGoal}")
            scores.add("${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}${progressBar}${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${TextUtil.colorPercentage(progressPercentage)}${progressPercentage}%")
        } else {
            val nextRank = RankHandler.getNextRank(user.getRank())
            if (nextRank != null) {
                val nextRankPrice = nextRank.getPrice(user.getPrestige())
                val formattedPrice = NumberUtils.format(nextRankPrice)

                val progressPercentage = if (user.hasMoneyBalance(nextRankPrice)) {
                    100.0
                } else {
                    (moneyBalance.toDouble() / nextRankPrice) * 100.0
                }

                val progressColor = ProgressBarBuilder.colorPercentage(progressPercentage)
                val progressBar = ProgressBarBuilder(char = '⬛').build(progressPercentage)

                scores.add("${padding}$primaryColor${ChatColor.BOLD}Rankup")
                scores.add("${padding}${ChatColor.GRAY}${user.getRank().displayName} ${ChatColor.GRAY}-> ${nextRank.displayName} ${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedPrice")
                scores.add("${padding}${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} $progressColor${progressPercentage.toInt()}%")
            }
        }

        renderBorders(user, scores)
    }

    private fun renderBorders(user: User, scores: MutableList<String>) {
        val primary: String
        val secondary: String

        if (user.settings.getSettingOption(UserSetting.RAINBOW_SCOREBOARD).getValue()) {
            primary = RainbowAnimation.getCurrentDisplay().toString()
            secondary = RainbowAnimation.getCurrentDisplay().toString()
        } else {
            primary = ChatColor.GRAY.toString()
            secondary = ChatColor.RED.toString()
        }

        scores.add(0, "$primary┌──────────────┐")
        scores.add(1, "")
        scores.add("")
        scores.add("      $secondary${ChatColor.BOLD}play.minejunkie.com")
        scores.add("$primary└──────────────┘")
    }

}
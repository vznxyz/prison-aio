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
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldownHandler
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.scoreboard.animation.LinkAnimation
import net.evilblock.prisonaio.module.user.scoreboard.slot.TeleportSlot
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object PrisonScoreGetter : ScoreGetter {

    // https://www.fileformat.info/info/unicode/block/box_drawing/list.htm
    // ╔═══════╗  ┌───────┐
    // ║       ║  │       │
    // ╚═══════╝  └───────┘

    val PRIMARY_COLOR = ChatColor.RED.toString()

    override fun getScores(scores: LinkedList<String>, player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (!user.settings.getSettingOption(UserSetting.SCOREBOARD_VISIBILITY).getValue<Boolean>()) {
            return
        }

        val padding = "  "

        if (EventGameHandler.isOngoingGame()) {
            val game = EventGameHandler.getOngoingGame()
            if (game != null) {
                if (game.isPlayingOrSpectating(player.uniqueId)) {
                    game.getScoreboardLines(player, scores)
                    renderBorders(scores)
                    return
                }
            }
        }

        scores.add("${padding}${PRIMARY_COLOR}${ChatColor.BOLD}${player.name}")

        val region = RegionHandler.findRegion(player.location)
        if (region is BitmaskRegion && region.hasBitmask(RegionBitmask.DANGER_ZONE)) {
            scores.add("${padding}${ChatColor.GREEN}⚔ ${ChatColor.GRAY}Kills: ${ChatColor.RED}${user.statistics.getKills()}")
            scores.add("${padding}${ChatColor.RED}⚔ ${ChatColor.GRAY}Deaths: ${ChatColor.RED}${user.statistics.getDeaths()}")

            val combatTimer = CombatTimerHandler.getTimer(player.uniqueId)
            if (combatTimer != null && !combatTimer.hasExpired()) {
                scores.add("${padding}${ChatColor.DARK_RED}${ChatColor.BOLD}⧗ ${ChatColor.GRAY}Combat: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(combatTimer.getRemainingSeconds().toInt())}")
            }

            val enderpearlCooldown = EnderpearlCooldownHandler.getCooldown(player.uniqueId)
            if (enderpearlCooldown != null && !enderpearlCooldown.hasExpired()) {
                scores.add("${padding}${ChatColor.YELLOW}${ChatColor.BOLD}⧗ ${ChatColor.GRAY}Enderpearl: ${ChatColor.RED}${enderpearlCooldown.getRemainingSeconds()}s")
            }

            val godAppleCooldown = GodAppleCooldownHandler.getCooldown(player.uniqueId)
            if (godAppleCooldown != null && !godAppleCooldown.hasExpired()) {
                scores.add("${padding}${ChatColor.GOLD}${ChatColor.BOLD}⧗ ${ChatColor.GRAY}Gopple: ${ChatColor.RED}${TimeUtil.formatIntoMMSS(godAppleCooldown.getRemainingSeconds().toInt())}")
            }

            if (TeleportSlot.canRender(player, user)) {
                scores.add("")
                scores.addAll(TeleportSlot.render(player, user))
            }

            renderBorders(scores)
            return
        }

        if (user.getPrestige() == 0) {
            scores.add("${padding}${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}No Prestige")
        } else {
            scores.add("${padding}${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Prestige ${user.getPrestige()}")
        }

        val formattedMoneyBalance = NumberUtils.format(user.getMoneyBalance())
        scores.add("${padding}${ChatColor.GREEN}${Constants.MONEY_SYMBOL} ${ChatColor.GRAY}$formattedMoneyBalance")

        val formattedTokensBalance = NumberUtils.format(user.getTokenBalance())
        scores.add("${padding}${ChatColor.YELLOW}${Constants.TOKENS_SYMBOL} ${ChatColor.GRAY}$formattedTokensBalance")

        scores.add("")

        renderSlots(player, user, scores)
        renderBorders(scores)
    }

    private fun renderSlots(player: Player, user: User, scores: MutableList<String>) {
        val slots = arrayListOf<ScoreboardSlot>()
        slots.addAll(ScoreboardHandler.getSlots().filter { it.canRender(player, user) })

        if (user.scoreboardSlots.isNotEmpty()) {
            val renderable = user.scoreboardSlots.filter { it.canRender(player, user) }
            if (renderable.isNotEmpty()) {
                slots.add(renderable.maxBy { it.priority() }!!)
            }
        }

        if (slots.isNotEmpty()) {
            scores.addAll(slots.maxBy { it.priority() }!!.render(player, user))
        }
    }

    private fun renderBorders(scores: MutableList<String>) {
        scores.add(0, "${ChatColor.GRAY}┌──────────────┐")
        scores.add(1, "")
        scores.add("")
        scores.add("${PRIMARY_COLOR}${ChatColor.BOLD}${LinkAnimation.getCurrentLink()}")
        scores.add("${ChatColor.GRAY}└──────────────┘")
    }

}
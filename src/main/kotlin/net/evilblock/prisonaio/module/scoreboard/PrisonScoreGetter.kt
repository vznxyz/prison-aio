package net.evilblock.prisonaio.module.scoreboard

import net.evilblock.cubed.scoreboard.ScoreGetter
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.ScoreboardVisibilitySettingOption
import net.evilblock.prisonaio.util.Constants
import org.bukkit.Bukkit
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

        if (!user.getSettingOption<ScoreboardVisibilitySettingOption>(UserSetting.SCOREBOARD_VISIBILITY).getValue<Boolean>()) {
            return
        }

        scores.add("")
        scores.add("  ${ChatColor.RED}${ChatColor.BOLD}${player.name}")
        scores.add("  ${ChatColor.RED}${Constants.RANK_SYMBOL} ${ChatColor.GRAY}Rank ${user.getCurrentRank().displayName}")

        if (user.getCurrentPrestige() == 0) {
            scores.add("  ${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Not Prestiged")
        } else {
            scores.add("  ${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Prestige ${user.getCurrentPrestige()}")
        }

        val moneyBalance = VaultHook.useEconomyAndReturn { economy -> economy.getBalance(Bukkit.getOfflinePlayer(user.uuid)) }
        val formattedMoneyBalance = NumberUtils.format(moneyBalance)
        scores.add("  ${ChatColor.RED}${Constants.MONEY_SYMBOL} ${ChatColor.GRAY}$formattedMoneyBalance")

        val formattedTokensBalance = NumberUtils.format(user.getTokensBalance())
        scores.add("  ${ChatColor.RED}${Constants.TOKENS_SYMBOL} ${ChatColor.GRAY}$formattedTokensBalance")
        scores.add("")

        val optionalNextRank = RankHandler.getNextRank(user.getCurrentRank())
        if (optionalNextRank.isPresent) {
            val nextRank = optionalNextRank.get()
            val formattedPrice = NumberUtils.format(nextRank.price)

            val progressPercentage = if (moneyBalance > nextRank.price) {
                100.0
            } else {
                (moneyBalance / nextRank.price) * 100.0
            }

            val progressColor = when {
                progressPercentage >= 100.0 -> {
                    ChatColor.GREEN
                }
                progressPercentage >= 30.0 -> {
                    ChatColor.YELLOW
                }
                else -> {
                    ChatColor.RED
                }
            }

            val progressBar = ProgressBarBuilder(char = '⬛').build(progressPercentage)

            scores.add("  ${ChatColor.RED}${ChatColor.BOLD}Progress")
            scores.add("  ${ChatColor.GRAY}${user.getCurrentRank().displayName} ${ChatColor.GRAY}-> ${nextRank.displayName} ${ChatColor.GRAY}(${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedPrice${ChatColor.GRAY})")
            scores.add("  ${ChatColor.GRAY}❙$progressBar${ChatColor.GRAY}❙ ($progressColor${progressPercentage.toInt()}%${ChatColor.GRAY})")
            scores.add("")
        }

//        scores.add(0, "${ChatColor.DARK_GRAY}╔═════════════╗")
        scores.add(0, "${ChatColor.DARK_GRAY}┌──────────────┐")
        scores.add("     ${ChatColor.RED}${ChatColor.ITALIC}store.minejunkie.com")
//        scores.add("${ChatColor.DARK_GRAY}╚═════════════╝")
        scores.add("${ChatColor.DARK_GRAY}└──────────────┘")
    }

}
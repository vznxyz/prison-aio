/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.logging.LogFile
import net.evilblock.cubed.logging.LogHandler
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import net.evilblock.prisonaio.module.minigame.coinflip.task.CoinFlipGameTicker
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import java.io.File
import java.util.*

object CoinFlipHandler : PluginHandler() {

    val CHAT_PREFIX: String = "${ChatColor.GRAY}[${ChatColor.AQUA}${ChatColor.BOLD}COIN${ChatColor.YELLOW}${ChatColor.BOLD}FLIP${ChatColor.GRAY}] "
    val PRIMARY_COLOR = ChatColor.WHITE
    const val PRIMARY_COLOR_ID = 0.toByte()
    val SECONDARY_COLOR = ChatColor.RED
    const val SECONDARY_COLOR_ID = 14.toByte()

    var disabled: Boolean = false
    val logFile: LogFile = LogFile(File(File(UserHandler.getModule().getPluginFramework().dataFolder, "logs"), "coinflip.txt"))

    private val games: MutableMap<UUID, CoinFlipGame> = hashMapOf()

    override fun getModule(): PluginModule {
        return MinigamesModule
    }

    override fun initialLoad() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val games = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<List<CoinFlipGame>>() {}.type) as List<CoinFlipGame>
                for (game in games) {
                    trackGame(game)
                }
            }
        }

        LogHandler.trackLogFile(logFile)

        Tasks.asyncTimer(CoinFlipGameTicker, 4L, 4L)
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(games.values, object : TypeToken<List<CoinFlipGame>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

    fun cancelGames() {
        for (game in games.values.toList()) {
            if (game.stage != CoinFlipGame.Stage.FINISHED) {
                game.finishGame()
            }
        }
    }

    fun getGames(): List<CoinFlipGame> {
        return games.values.toList()
    }

    fun getGame(uuid: UUID): CoinFlipGame? {
        return games[uuid]
    }

    fun trackGame(game: CoinFlipGame) {
        games[game.uuid] = game
    }

    fun forgetGame(game: CoinFlipGame) {
        games.remove(game.uuid)
    }

    fun formatMoney(amount: Double): String {
        return ChatColor.translateAlternateColorCodes('&', getModule().config.getString("coinflip.money-format"))
            .replace("{amount}", NumberUtils.format(amount))
    }

    fun formatTokens(tokens: Long): String {
        return ChatColor.translateAlternateColorCodes('&', getModule().config.getString("coinflip.token-format"))
            .replace("{amount}", NumberUtils.format(tokens))
    }

    fun getMenuTitle(): String {
        return ChatColor.translateAlternateColorCodes('&', getModule().config.getString("coinflip.menu-title"))
    }

    fun getHighlightedGameThresholdMoney(): Double {
        return getModule().config.getDouble("coinflip.games.highlighted.threshold.money")
    }

    fun getHighlightedGameThresholdTokens(): Long {
        return getModule().config.getLong("coinflip.games.highlighted.threshold.tokens")
    }

    fun getMinBetMoney(): Double {
        return getModule().config.getDouble("coinflip.rules.min-bet.money")
    }

    fun getMinBetTokens(): Long {
        return getModule().config.getLong("coinflip.rules.min-bet.tokens")
    }

    fun getMaxBetMoney(): Double {
        return getModule().config.getDouble("coinflip.rules.max-bet.money")
    }

    fun getMaxBetTokens(): Long {
        return getModule().config.getLong("coinflip.rules.max-bet.tokens")
    }

    fun renderStatisticsDisplay(user: User): List<String> {
        val list = arrayListOf<String>()

        list.add("${ChatColor.GRAY}Wins: ${ChatColor.GREEN}${NumberUtils.format(user.statistics.getCoinflipWins())}")
        list.add("${ChatColor.GRAY}Losses: ${ChatColor.RED}${NumberUtils.format(user.statistics.getCoinflipLosses())}")
        list.add("")

        val moneyProfit = user.statistics.getCoinflipProfit(Currency.Type.MONEY)

        val moneyProfitStyle = when {
            moneyProfit.toDouble() == 0.0 -> {
                ""
            }
            moneyProfit.toDouble() > 0 -> {
                "${ChatColor.GREEN}+"
            }
            else -> {
                "${ChatColor.RED}-"
            }
        }

        list.add("${ChatColor.GRAY}Net Profit (Money): $moneyProfitStyle${Currency.Type.MONEY.format(moneyProfit)}")

        val tokensProfit = user.statistics.getCoinflipProfit(Currency.Type.TOKENS)

        val tokensProfitStyle = when {
            tokensProfit.toLong() == 0L -> {
                ""
            }
            tokensProfit.toLong() > 0 -> {
                "${ChatColor.GREEN}+"
            }
            else -> {
                "${ChatColor.RED}-"
            }
        }

        list.add("${ChatColor.GRAY}Net Profit (Tokens): $tokensProfitStyle${Currency.Type.TOKENS.format(tokensProfit)}")
        list.add("")
        list.add("${ChatColor.GRAY}Last 24 Hrs: ${Currency.Type.MONEY.format(0)} ${ChatColor.GRAY}/ ${Currency.Type.TOKENS.format(0)}")
        list.add("${ChatColor.GRAY}Last 7 Days: ${Currency.Type.MONEY.format(0)} ${ChatColor.GRAY}/ ${Currency.Type.TOKENS.format(0)}")

        return list
    }

}
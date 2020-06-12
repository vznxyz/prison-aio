package net.evilblock.prisonaio.module.minigame.coinflip

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import net.evilblock.prisonaio.module.minigame.coinflip.task.CoinFlipGameTicker
import org.bukkit.ChatColor
import java.util.*

object CoinFlipHandler : PluginHandler {

    val PRIMARY_COLOR = ChatColor.WHITE
    val PRIMARY_COLOR_ID = 0.toByte()
    val SECONDARY_COLOR = ChatColor.RED
    val SECONDARY_COLOR_ID = 14.toByte()

    private val games: MutableMap<UUID, CoinFlipGame> = hashMapOf()

    override fun getModule(): PluginModule {
        return MinigamesModule
    }

    override fun initialLoad() {
        getModule().getPlugin().server.scheduler.runTaskTimerAsynchronously(getModule().getPlugin(), CoinFlipGameTicker, 4L, 4L)
    }

    fun cancelGames() {
        for (game in games.values.toList()) {
            game.finishGame()
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

    fun getGuideTitle(): String {
        return ChatColor.translateAlternateColorCodes('&', getModule().config.getString("coinflip.guide-title"))
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

}
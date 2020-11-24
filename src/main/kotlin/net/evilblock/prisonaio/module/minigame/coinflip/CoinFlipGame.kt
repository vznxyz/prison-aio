/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip

import net.evilblock.cubed.logging.ErrorHandler
import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.round

class CoinFlipGame(
    val creator: User,
    var opponent: User? = null,
    val currencyAmount: Number,
    val currency: Currency
) {

    val uuid: UUID = UUID.randomUUID()

    var stage: Stage = Stage.WAITING_FOR_OPPONENT
    var stageTicks: Int = 0

    val watchers: MutableSet<UUID> = hashSetOf()

    var winner: User? = null
    var loser: User? = null

    private var rollIndex = 0
    private var rollSequence = arrayListOf(3, 3, 3, 3, 4)

    fun isCreator(player: Player): Boolean {
        return creator.uuid == player.uniqueId
    }

    fun isHighlighted(): Boolean {
        return if (currency == Currency.Type.MONEY) {
            currencyAmount.toDouble() >= CoinFlipHandler.getHighlightedGameThresholdMoney()
        } else {
            currencyAmount.toLong() >= CoinFlipHandler.getHighlightedGameThresholdTokens()
        }
    }

    fun getColorInvertAnimationSwitch(): Boolean {
        return rollIndex % 2 == 0
    }

    fun isWaitingForOpponent(): Boolean {
        return stage == Stage.WAITING_FOR_OPPONENT
    }

    fun isStartingSoon(): Boolean {
        return stage == Stage.WAITING_FOR_SPECTATORS && stageTicks >= 15
    }

    fun getSecondsRemaining(): Int {
        return round((30 - stageTicks) / 5.0).toInt()
    }

    fun finishGame() {
        if (winner == null) {
            sendMessage("${CoinFlipHandler.CHAT_PREFIX}There was no winner, so both players have been refunded!")

            try {
                currency.give(creator.uuid, currencyAmount)
            } catch (e: Exception) {
                val errorId = ErrorHandler.generateErrorLog(
                    errorType = "cfFinishGame",
                    event = generateDataMap(),
                    exception = e
                )

                CoinFlipHandler.logFile.commit("Failed to refund creator ${creator.getUsername()} (${creator.uuid}) of game ($uuid). See exception at /plugins/Cubed/errors/${errorId}.txt")
            }

            if (opponent != null) {
                try {
                    currency.give(opponent!!.uuid, currencyAmount)
                } catch (e: Exception) {
                    val errorId = ErrorHandler.generateErrorLog(
                        errorType = "cfFinishGame",
                        event = generateDataMap(),
                        exception = e
                    )

                    CoinFlipHandler.logFile.commit("Failed to refund opponent ${opponent!!.getUsername()} (${opponent!!.uuid}) of game ($uuid). See exception at /plugins/Cubed/errors/${errorId}.txt")
                }
            }
        } else {
            sendMessage("${CoinFlipHandler.CHAT_PREFIX}${ChatColor.GREEN}${ChatColor.BOLD}${winner!!.getUsername()} ${ChatColor.GRAY}won the game for ${currency.format(NumberUtils.numberOperation(currencyAmount, currencyAmount, true))}${ChatColor.GRAY}!")

            try {
                winner!!.statistics.addCoinflipWin()
                winner!!.statistics.addCoinflipProfit(currency.toType(), currencyAmount)

                currency.give(winner!!.uuid, currencyAmount)
                currency.give(winner!!.uuid, currencyAmount)
            } catch (e: Exception) {
                val errorId = ErrorHandler.generateErrorLog(
                    errorType = "cfFinishGame",
                    event = generateDataMap(),
                    exception = e
                )

                CoinFlipHandler.logFile.commit("Failed to payout winner ${winner!!.getUsername()} (${winner!!.uuid}) of game ($uuid). See exception at /plugins/Cubed/errors/${errorId}.txt")
            }

            if (opponent != null) {
                try {
                    opponent!!.statistics.addCoinflipLoss()
                    opponent!!.statistics.subtractCoinflipProfit(currency.toType(), currencyAmount)
                } catch (e: Exception) {
                    val errorId = ErrorHandler.generateErrorLog(
                        errorType = "cfFinishGame",
                        event = generateDataMap(),
                        exception = e
                    )

                    CoinFlipHandler.logFile.commit("Failed to update stats of loser ${opponent!!.getUsername()} (${winner!!.uuid}) of game ($uuid). See exception at /plugins/Cubed/errors/${errorId}.txt")
                }
            }
        }

        CoinFlipHandler.forgetGame(this)
    }

    fun tick() {
        stageTicks++

        when (stage) {
            Stage.WAITING_FOR_OPPONENT -> {
                if (opponent != null) {
                    stage = Stage.WAITING_FOR_SPECTATORS
                    stageTicks = 0
                }
            }
            Stage.WAITING_FOR_SPECTATORS -> {
                if (stageTicks >= 30) { // 6 seconds, because a cf game tick is every 4 minecraft ticks
                    stage = Stage.ROLLING
                    stageTicks = 0

                    winner = if (Chance.random()) {
                        creator
                    } else {
                        opponent!!
                    }

                    loser = if (creator == winner) {
                        opponent
                    } else {
                        creator
                    }

                    if (winner == opponent!!) {
                        rollSequence.add(4)
                    }
                }
            }
            Stage.ROLLING -> {
                if (stageTicks >= rollSequence[rollIndex]) {
                    rollIndex++
                    stageTicks = 0

                    if (rollIndex >= rollSequence.size) {
                        stage = Stage.FINISHED
                        stageTicks = 0
                    }
                }
            }
            Stage.FINISHED -> {
                if (stageTicks >= 15) {
                    finishGame()
                }
            }
        }
    }

    fun sendMessage(message: String) {
        creator.getPlayer()?.sendMessage(message)
        opponent?.getPlayer()?.sendMessage(message)

        for (watcher in watchers) {
            if (watcher == creator.uuid) {
                continue
            }

            if (opponent != null && watcher == opponent!!.uuid) {
                continue
            }

            Bukkit.getPlayer(watcher)?.sendMessage(message)
        }
    }

    private fun generateDataMap(): Map<String, String> {
        return hashMapOf(
            "gameId" to uuid.toString(),
            "currencyType" to currency.toType().name,
            "currencyAmount" to currencyAmount.toString(),
            "creator" to creator.uuid.toString()
        ).also {
            if (opponent != null) {
                it["opponent"] = opponent!!.uuid.toString()
            }

            if (winner != null) {
                it["winner"] = winner!!.uuid.toString()
            }
        }
    }

    enum class Stage {
        WAITING_FOR_OPPONENT,
        WAITING_FOR_SPECTATORS,
        ROLLING,
        FINISHED
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.round

class CoinFlipGame(
    val creator: User,
    var opponent: User? = null,
    val value: Currency<*>
) {

    val uuid: UUID = UUID.randomUUID()

    var stage: Stage = Stage.WAITING_FOR_OPPONENT
    var stageTicks: Int = 0
    var winner: User? = null

    private var rollIndex = 0
    private var rollSequence = arrayListOf(3, 3, 3, 3, 4)

    fun isCreator(player: Player): Boolean {
        return creator.uuid == player.uniqueId
    }

    fun isHighlighted(): Boolean {
        if (value.isMoney()) {
            if (value.double() >= CoinFlipHandler.getHighlightedGameThresholdMoney()) {
                return true
            }
        } else if (value.isTokens()) {
            if (value.long() >= CoinFlipHandler.getHighlightedGameThresholdTokens()) {
                return true
            }
        }

        return false
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
            value.give(Bukkit.getOfflinePlayer(creator.uuid))
        } else {
            value.give(Bukkit.getOfflinePlayer(winner!!.uuid))
            value.give(Bukkit.getOfflinePlayer(winner!!.uuid))
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

    enum class Stage {
        WAITING_FOR_OPPONENT,
        WAITING_FOR_SPECTATORS,
        ROLLING,
        FINISHED
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.roll

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import net.evilblock.prisonaio.module.crate.placed.PlacedCrate
import org.bukkit.entity.Player

class CrateRoll(val placedCrate: PlacedCrate) {

    private val winnings = arrayListOf<CrateReward>()

    init {
        calculateRoll()
    }

    fun finish(player: Player) {
        for (reward in winnings) {
            reward.execute(player)
        }
    }

    private fun calculateRoll() {
        if (!placedCrate.crate.isSetup()) {
            throw IllegalStateException("Cannot calculate roll if crate has no rewards or no rewards with a chance above 0.0 (ID: ${placedCrate.crate.getRawName()})")
        }

//        val showcase = arrayListOf<CrateReward>()
//
//        w@ while (showcase.size < 10) {
//            for (reward in placedCrate.crate.rewards.shuffled()) {
//                if (Chance.percent(reward.chance)) {
//                    showcase.add(reward)
//                }
//            }
//        }

        val winnings = arrayListOf<CrateReward>()
        var amountOfRewards = placedCrate.crate.rewardsRange.first
        val rewardsRange = placedCrate.crate.rewardsRange.last - placedCrate.crate.rewardsRange.first
        var extraRewardChance = 10.0
        for (i in 0 until rewardsRange) {
            if (Chance.percent(extraRewardChance)) {
                amountOfRewards++
                extraRewardChance /= 10.0
            } else {
                break
            }
        }

        amountOfRewards = amountOfRewards.coerceAtMost(placedCrate.crate.rewardsRange.last)

        w@ while (winnings.size < amountOfRewards) {
            for (reward in placedCrate.crate.rewards.shuffled()) {
                if (Chance.percent(reward.chance)) {
                    winnings.add(reward)

                    if (winnings.size >= amountOfRewards) {
                        break@w
                    }
                }
            }
        }

        this.winnings.clear()
        this.winnings.addAll(winnings)
    }

}
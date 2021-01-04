/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.LuckyBlockReward
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min

class LuckyBlock(val id: String) {

    var name: String = id
    var blockType: ItemStack = ItemStack(Material.GLASS)
    var spawnChance: Double = 0.0

    var rewards: MutableList<LuckyBlockReward> = arrayListOf()
    var minRewards: Int = 0
    var maxRewards: Int = 0

    var skinSource: String? = null

    fun isSetup(): Boolean {
        return spawnChance > 0.0 && skinSource != null && rewards.isNotEmpty()
    }

    fun giveRewards(player: Player) {
        if (rewards.isEmpty()) {
            return
        }

        if (maxRewards <= 0) {
            return
        }

        val amountOfWinnings = if (minRewards == maxRewards) {
            minRewards
        } else {
            ThreadLocalRandom.current().nextInt(min(minRewards, maxRewards), max(minRewards, maxRewards))
        }

        val winnings = arrayListOf<LuckyBlockReward>()

        // add all the rewards that have a 100% chance of winning
        winnings.addAll(rewards.filter { it.chance >= 100.0 })

        if (winnings.size < amountOfWinnings) {
            while (winnings.size < amountOfWinnings) {
                winnings.add(Chance.weightedPick(rewards.filter { it.chance < 100.0 }) { it.chance })
            }
        }

        if (winnings.isNotEmpty()) {
            for (reward in winnings) {
                reward.execute(player)
            }
        } else {
            player.sendMessage("${MinesModule.getLuckyBlockChatPrefix()}You didn't receive any rewards!")
        }
    }

}
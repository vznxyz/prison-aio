/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock

import net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.LuckyBlockReward
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class LuckyBlock(val id: String) {

    var name: String = id
    var blockType: ItemStack = ItemStack(Material.GLASS)
    var rewards: MutableList<LuckyBlockReward> = arrayListOf()
    var spawnChance: Double = 0.0
    var skinSource: String? = null

    fun isSetup(): Boolean {
        return spawnChance > 0.0 && skinSource != null
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement

import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface DeliveryManRewardRequirementType<T : DeliveryManRewardRequirement> {

    fun getName(): String

    fun getDescription(): String

    fun getIcon(): ItemStack

    fun startSetupProcedure(player: Player, reward: DeliveryManReward)

    fun isCompatibleWithReward(reward: DeliveryManReward): Boolean {
        return true
    }

}
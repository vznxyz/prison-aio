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
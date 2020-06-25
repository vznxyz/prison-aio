/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward

import net.evilblock.prisonaio.module.reward.deliveryman.reward.cooldown.DeliveryManCooldown
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirement
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class DeliveryManReward(
    val id: String,
    var name: String,
    var order: Int = 0
) {

    var cooldown: DeliveryManCooldown = DeliveryManCooldown.DAILY
    var commands: MutableList<String> = arrayListOf()
    val requirements: MutableList<DeliveryManRewardRequirement> = arrayListOf()
    var rewardsText = arrayListOf<String>()

    fun meetsRequirements(player: Player): Boolean {
        for (requirement in requirements) {
            if (!requirement.test(player)) {
                return false
            }
        }
        return true
    }

    fun execute(player: Player) {
        for (command in commands) {
            val processedCommand = command
                .replace("{playerName}", player.name)
                .replace("{playerUuid}", player.uniqueId.toString())
                .replace("{rewardId}", this.id)
                .replace("{rewardName}", this.name)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.impl

import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.reward.deliveryman.reward.menu.EditDeliveryManRewardRequirementsMenu
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirement
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirementType
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

class BlocksMinedRequirement(private var blocksMined: Int) : DeliveryManRewardRequirement {

    override fun getText(): String {
        return "Mine ${NumberFormat.getInstance().format(blocksMined)} blocks"
    }

    override fun getType(): DeliveryManRewardRequirementType<BlocksMinedRequirement> {
        return BlocksMinedRequirementType
    }

    override fun test(player: Player): Boolean {
        return UserHandler.getUser(player.uniqueId).statistics.getBlocksMined() >= blocksMined
    }

    object BlocksMinedRequirementType : DeliveryManRewardRequirementType<BlocksMinedRequirement> {
        override fun getName(): String {
            return "Blocks Mined"
        }

        override fun getDescription(): String {
            return "Reach a certain amount of blocks progress"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.DIAMOND_PICKAXE)
        }

        override fun startSetupProcedure(player: Player, reward: DeliveryManReward) {
            NumberPrompt().acceptInput { number ->
                assert(number.toInt() > 0)

                reward.requirements.add(BlocksMinedRequirement(number.toInt()))

                Tasks.async {
                    DeliveryManHandler.saveData()
                }

                player.sendMessage("${ChatColor.GREEN}Successfully added requirement.")

                EditDeliveryManRewardRequirementsMenu(reward).openMenu(player)
            }.start(player)
        }

        override fun isCompatibleWithReward(reward: DeliveryManReward): Boolean {
            return reward.requirements.firstOrNull { it is BlocksMinedRequirement } == null
        }
    }

}
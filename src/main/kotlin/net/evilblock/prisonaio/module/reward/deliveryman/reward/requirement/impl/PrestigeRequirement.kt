/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.impl

import net.evilblock.cubed.util.bukkit.ConversationUtil
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

class PrestigeRequirement(private var prestige: Int) : DeliveryManRewardRequirement {

    override fun getText(): String {
        return "Reach prestige $prestige"
    }

    override fun getType(): DeliveryManRewardRequirementType<PrestigeRequirement> {
        return PrestigeRequirementType
    }

    override fun test(player: Player): Boolean {
        return UserHandler.getUser(player.uniqueId).getCurrentPrestige() >= prestige
    }

    object PrestigeRequirementType : DeliveryManRewardRequirementType<PrestigeRequirement> {
        override fun getName(): String {
            return "Prestige"
        }

        override fun getDescription(): String {
            return "Reach a certain level of prestige"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.NETHER_STAR)
        }

        override fun startSetupProcedure(player: Player, reward: DeliveryManReward) {
            ConversationUtil.startConversation(player, NumberPrompt { number ->
                assert(number > 0)

                reward.requirements.add(PrestigeRequirement(number))
                DeliveryManHandler.saveData()

                player.sendMessage("${ChatColor.GREEN}Successfully added requirement.")

                EditDeliveryManRewardRequirementsMenu(reward).openMenu(player)
            })
        }

        override fun isCompatibleWithReward(reward: DeliveryManReward): Boolean {
            return reward.requirements.firstOrNull { it is PrestigeRequirement } == null
        }
    }

}
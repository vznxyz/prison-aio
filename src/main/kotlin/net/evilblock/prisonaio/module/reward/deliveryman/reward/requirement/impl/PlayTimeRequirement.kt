package net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.impl

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.prompt.DurationPrompt
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

class PlayTimeRequirement(private var duration: Long) : DeliveryManRewardRequirement {

    override fun getText(): String {
        return "Play on the server for ${TimeUtil.formatIntoAbbreviatedString((duration / 1000.0).toInt())}"
    }

    override fun getType(): DeliveryManRewardRequirementType<PlayTimeRequirement> {
        return PlayTimeRequirementType
    }

    override fun test(player: Player): Boolean {
        return UserHandler.getUser(player.uniqueId).statistics.getLivePlayTime() >= duration
    }

    object PlayTimeRequirementType : DeliveryManRewardRequirementType<PlayTimeRequirement> {
        override fun getName(): String {
            return "Play Time"
        }

        override fun getDescription(): String {
            return "Reach a certain amount of time played"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.WATCH)
        }

        override fun startSetupProcedure(player: Player, reward: DeliveryManReward) {
            ConversationUtil.startConversation(player, DurationPrompt { duration ->
                assert(duration > 0)

                reward.requirements.add(PlayTimeRequirement(duration))
                DeliveryManHandler.saveData()

                player.sendMessage("${ChatColor.GREEN}Successfully added requirement.")

                EditDeliveryManRewardRequirementsMenu(reward).openMenu(player)
            })
        }

        override fun startEditProcedure(player: Player, reward: DeliveryManReward, requirement: DeliveryManRewardRequirement) {
            ConversationUtil.startConversation(player, DurationPrompt { duration ->
                assert(duration > 0)

                (requirement as PlayTimeRequirement).duration = duration
                DeliveryManHandler.saveData()

                player.sendMessage("${ChatColor.GREEN}Successfully updated requirement.")

                EditDeliveryManRewardRequirementsMenu(reward).openMenu(player)
            })
        }

        override fun isCompatibleWithReward(reward: DeliveryManReward): Boolean {
            return reward.requirements.firstOrNull { it is PlayTimeRequirement } == null
        }
    }

}
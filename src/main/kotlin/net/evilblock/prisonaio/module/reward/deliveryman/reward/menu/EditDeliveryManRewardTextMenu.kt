/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditDeliveryManRewardTextMenu(private val reward: DeliveryManReward) : TextEditorMenu(lines = reward.rewardsText.toMutableList()) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Rewards Text - ${reward.name}"
    }

    override fun onSave(player: Player, list: List<String>) {
        reward.rewardsText = ArrayList(lines)
        DeliveryManHandler.saveData()
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditDeliveryManRewardMenu(reward).openMenu(player)
        }
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new text.")
            .charLimit(100)
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import net.evilblock.prisonaio.module.crate.reward.menu.button.CrateRewardButton
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CratePreviewMenu(private val crate: Crate) : Menu() {

    override fun getTitle(player: Player): String {
        return "Preview of ${crate.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (reward in crate.rewards.sortedBy { it.sortOrder }) {
            buttons[buttons.size] = RewardButton(reward)
        }

        return buttons
    }

    private inner class RewardButton(reward: CrateReward) : CrateRewardButton(reward) {
        override fun getName(player: Player): String {
            return reward.name
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (reward.getIcon().hasItemMeta() && reward.getIcon().itemMeta.hasDisplayName() && reward.getIcon().itemMeta.displayName != reward.name) {
                description.add(reward.getIcon().itemMeta.displayName)
            }

            if (reward.getIcon().hasItemMeta() && reward.getIcon().itemMeta.hasLore()) {
                description.addAll(reward.getIcon().lore!!.toList())
            }

            if (CratesModule.isShowChancesInPreviewMenu()) {
                description.add("")
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Chance: ${ChatColor.YELLOW}${reward.chance}%")
            }

            return description
        }
    }

}
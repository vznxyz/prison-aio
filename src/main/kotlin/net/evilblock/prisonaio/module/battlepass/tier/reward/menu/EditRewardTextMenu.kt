/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.tier.reward.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditRewardTextMenu(private val reward: Reward) : TextEditorMenu(reward.textLines) {

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new text.")
            .charLimit(100)
    }

    override fun onSave(player: Player, list: List<String>) {
        reward.textLines = lines.map { ChatColor.translateAlternateColorCodes('&', it) }.toMutableList()
        TierHandler.saveData()
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditRewardMenu(reward).openMenu(player)
        }
    }

}
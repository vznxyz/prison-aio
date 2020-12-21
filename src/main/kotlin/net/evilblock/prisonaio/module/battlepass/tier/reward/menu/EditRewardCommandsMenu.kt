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

class EditRewardCommandsMenu(private val reward: Reward) : TextEditorMenu(reward.commands) {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Reward Commands"
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new command.")
            .charLimit(100)
    }

    override fun onSave(player: Player, list: List<String>) {
        reward.commands = lines.toMutableList()
        TierHandler.saveData()
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditRewardMenu(reward).openMenu(player)
        }
    }

}
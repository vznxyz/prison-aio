/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.prompt

import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.menu.MineManageEffectsMenu
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class MineEditEffectPotencyPrompt(private val mine: Mine, private val potionEffectType: PotionEffectType) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GREEN}Please input a new potency value."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        try {
            // parse input to int
            val inputNumber = input.toInt()

            // check if input is within range
            if (inputNumber !in 1..100) {
                context.forWhom.sendRawMessage("${ChatColor.RED}The potency must be a number between ${ChatColor.WHITE}1 - 100${ChatColor.RED}.")
                return this
            }

            // set effect's potency to input
            mine.effectsConfig.effectPotency[potionEffectType] = inputNumber

            // save changes to file
            MineHandler.saveData()

            // send update message
            context.forWhom.sendRawMessage("${ChatColor.GREEN}Updated effect's potency.")
        } catch (e: NumberFormatException) {
            context.forWhom.sendRawMessage("${ChatColor.RED}The number you input is not valid.")
            context.forWhom.sendRawMessage("${ChatColor.RED}Example input: 99")
        }

        // re-open the menu for better ux
        MineManageEffectsMenu(mine).openMenu(player = context.forWhom as Player)

        return Prompt.END_OF_CONVERSATION
    }

}
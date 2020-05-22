package net.evilblock.prisonaio.module.mine.prompt

import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.menu.MineManageResetMenu
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player

class MineEditResetIntervalPrompt(private val mine: Mine) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GREEN}Please provide a new reset interval in seconds."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        try {
            // parse input to int
            val inputNumber = input.toInt()

            // check if input is within range
            if (inputNumber !in 1..Int.MAX_VALUE) {
                context.forWhom.sendRawMessage("${ChatColor.RED}The reset interval must be a number between ${ChatColor.WHITE}1 - ${Int.MAX_VALUE}${ChatColor.RED}.")
                return this
            }

            // set reset interval to input
            mine.resetConfig.resetInterval = inputNumber

            // save changes to file
            MineHandler.saveData()

            // send update message
            context.forWhom.sendRawMessage("${ChatColor.GREEN}Updated mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}'s reset interval.")
        } catch (e: NumberFormatException) {
            context.forWhom.sendRawMessage("${ChatColor.RED}The number you input is not valid.")
            context.forWhom.sendRawMessage("${ChatColor.RED}Example input: 99")
        }

        // re-open the menu for better ux
        MineManageResetMenu(mine).openMenu(player = context.forWhom as Player)

        return Prompt.END_OF_CONVERSATION
    }

}
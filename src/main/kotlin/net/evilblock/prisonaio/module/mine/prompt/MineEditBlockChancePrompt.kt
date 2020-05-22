package net.evilblock.prisonaio.module.mine.prompt

import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.block.BlockType
import net.evilblock.prisonaio.module.mine.menu.MineManageBlocksMenu
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player

class MineEditBlockChancePrompt(private val mine: Mine, private val blockType: BlockType) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GREEN}Please provide a new percentage value."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        if (input.contains("%")) {
            input.replace("%", "")
        }

        try {
            // parse input to double
            val inputNumber = input.toDouble()

            // set block type's percentage to input
            blockType.percentage = inputNumber

            // save changes to file
            MineHandler.saveData()

            // send update message
            context.forWhom.sendRawMessage("${ChatColor.GREEN}Updated block's chance percentage.")
        } catch (e: NumberFormatException) {
            context.forWhom.sendRawMessage("${ChatColor.RED}The percentage you input is not valid.")
            context.forWhom.sendRawMessage("${ChatColor.RED}Example input: `99.9%`")
        }

        // re-open the menu for better ux
        MineManageBlocksMenu(mine).openMenu(player = context.forWhom as Player)

        return Prompt.END_OF_CONVERSATION
    }

}
package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.ConversationIdentity
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ConversationDialogue(private val from: ConversationIdentity, private val to: ConversationIdentity, val message: String, delay: Long = 3000L) : Dialogue(delay) {

    override fun send(player: Player) {
        player.sendMessage(" ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${from.getName()} ${ChatColor.GRAY}-> ${to.getName()}${ChatColor.GRAY})")
        player.sendMessage(" ${ChatColor.GRAY}$message")
    }

}
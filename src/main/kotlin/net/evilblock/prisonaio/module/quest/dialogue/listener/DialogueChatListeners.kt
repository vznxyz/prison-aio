package net.evilblock.prisonaio.module.quest.dialogue.listener

import net.evilblock.prisonaio.module.quest.QuestHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object DialogueChatListeners : Listener {

    /**
     * Prevents player chat messages being sent to players receiving NPC dialogue.
     */
    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        if (QuestHandler.isDialogueSequencePlaying(event.player)) {
            event.isCancelled = true
            return
        }

        val recipientIterator = event.recipients.iterator()
        while (recipientIterator.hasNext()) {
            val recipient = recipientIterator.next()
            if (QuestHandler.isDialogueSequencePlaying(recipient)) {
                recipientIterator.remove()
            }
        }
    }

}
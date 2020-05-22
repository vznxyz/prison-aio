package net.evilblock.prisonaio.module.quest.dialogue.listener

import net.evilblock.prisonaio.module.quest.QuestHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent

object DialogueSequenceListeners : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if (QuestHandler.isDialogueSequencePlaying(event.player)) {
            QuestHandler.stopDialogueSequence(event.player)
        }
    }

    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (QuestHandler.isDialogueSequencePlaying(event.player)) {
            event.isCancelled = true
        }
    }

}
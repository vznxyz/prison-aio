/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.dialogue

import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.ConversationDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.PlayerConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.StaticConversationIdentity
import org.bukkit.entity.Player
import java.util.*

class TutorialStartDialogue(private val uuid: UUID) : DialogueSequence {

    override fun getSequence(player: Player): LinkedList<Dialogue> {
        val sequence = LinkedList<Dialogue>()

        val guideIdentity = StaticConversationIdentity(QuestsModule.getNpcName("tutorial-guide"))
        val playerIdentity = PlayerConversationIdentity(uuid)

        sequence.add(ConversationDialogue(from = guideIdentity, to = playerIdentity, message = "Line 1", delay = 3500L))
        sequence.add(ConversationDialogue(from = guideIdentity, to = playerIdentity, message = "Line 2", delay = 4000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = guideIdentity, message = "Line 3", delay = 3000L))
        sequence.add(ConversationDialogue(from = guideIdentity, to = playerIdentity, message = "Line 4", delay = 3000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = guideIdentity, message = "Line 5", delay = 4000L))
        sequence.add(ConversationDialogue(from = guideIdentity, to = playerIdentity, message = "Line 6", delay = 8000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = guideIdentity, message = "Line 7", delay = 6000L))
        sequence.add(ConversationDialogue(from = guideIdentity, to = playerIdentity, message = "Line 8", delay = 2000L))

        return sequence
    }

}
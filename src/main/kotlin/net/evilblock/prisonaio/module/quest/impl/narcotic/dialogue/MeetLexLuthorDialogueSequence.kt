package net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.DialoguePlayer
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.ConversationDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.PlayerConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.StaticConversationIdentity
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class MeetLexLuthorDialogueSequence(private val uuid: UUID) : DialogueSequence {

    override fun getSequence(player: Player): LinkedList<Dialogue> {
        val sequence = LinkedList<Dialogue>()

        val lexLuthorIdentity = StaticConversationIdentity("${ChatColor.YELLOW}${ChatColor.BOLD}Lex Luthor")
        val playerIdentity = PlayerConversationIdentity(uuid)

        sequence.add(ConversationDialogue(from = lexLuthorIdentity, to = playerIdentity, message = "What's up bro! Listen man, I have a job for you if you're interested.", delay = 3500L))
        sequence.add(ConversationDialogue(from = lexLuthorIdentity, to = playerIdentity, message = "A friend of mine has some stuff that needs to get from one place to another.", delay = 4000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = lexLuthorIdentity, message = "Well... what am I moving?", delay = 3000L))
        sequence.add(ConversationDialogue(from = lexLuthorIdentity, to = playerIdentity, message = "The less you know, the better.", delay = 3000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = lexLuthorIdentity, message = "Sounds sketchy... but.. I guess I'm in. What do I need to do?", delay = 4000L))
        sequence.add(ConversationDialogue(from = lexLuthorIdentity, to = playerIdentity, message = "Alright man, that's what I like to hear! I need you to visit my friend Pablo, who is located near the Basketball Court (-97, 56, -59). He will give you the stuff and tell you where to move it.", delay = 8000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = lexLuthorIdentity, message = "Ok. That sounds simple enough. Well, I'll catch you after I get back I guess.", delay = 6000L))
        sequence.add(ConversationDialogue(from = lexLuthorIdentity, to = playerIdentity, message = "Good luck bro!", delay = 2000L))

        return sequence
    }

}
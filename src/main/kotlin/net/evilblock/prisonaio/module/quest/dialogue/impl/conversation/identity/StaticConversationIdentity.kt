package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity

class StaticConversationIdentity(private val name: String) : ConversationIdentity {

    override fun getName(): String {
        return name
    }

}
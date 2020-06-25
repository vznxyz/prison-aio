/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity

class StaticConversationIdentity(private val name: String) : ConversationIdentity {

    override fun getName(): String {
        return name
    }

}
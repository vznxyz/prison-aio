/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity

import net.evilblock.cubed.entity.npc.NpcEntity

class NpcConversationIdentity(val npc: NpcEntity) : ConversationIdentity {

    private val name = npc.getLines().first()

    override fun getName(): String {
        return name
    }

}
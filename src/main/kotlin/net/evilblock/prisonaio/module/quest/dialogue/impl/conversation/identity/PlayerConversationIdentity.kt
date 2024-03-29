/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity

import net.evilblock.cubed.Cubed
import org.bukkit.ChatColor
import java.util.*

class PlayerConversationIdentity(private val playerUuid: UUID) : ConversationIdentity {

    override fun getName(): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}${Cubed.instance.uuidCache.name(playerUuid)}"
    }

}
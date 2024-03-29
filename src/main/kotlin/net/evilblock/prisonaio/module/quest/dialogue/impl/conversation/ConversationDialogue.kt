/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.ConversationIdentity
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ConversationDialogue(
    private val from: ConversationIdentity,
    private val to: ConversationIdentity,
    val message: String,
    delay: Long = 0L,
    useState: Boolean = false
) : Dialogue(delay, useState) {

    override fun send(player: Player) {
        player.sendMessage(" ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${from.getName()} ${ChatColor.GRAY}-> ${to.getName()}${ChatColor.GRAY})")
        player.sendMessage(" ${ChatColor.GRAY}$message")
    }

}
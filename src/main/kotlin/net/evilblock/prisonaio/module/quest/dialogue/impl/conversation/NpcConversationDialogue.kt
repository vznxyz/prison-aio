/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl.conversation

import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.ConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.NpcConversationIdentity
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class NpcConversationDialogue(
    private val from: ConversationIdentity,
    private val to: ConversationIdentity,
    val message: String,
    delay: Long = 0L,
    useState: Boolean = false
) : Dialogue(delay, useState) {

    override fun send(player: Player) {
        if (from is NpcConversationIdentity) {
            val lines = arrayListOf(from.getName())
            lines.addAll(TextSplitter.split(length = 40, text = message.replace("${ChatColor.GRAY}", "${ChatColor.WHITE}"), linePrefix = ChatColor.WHITE.toString()))

            from.npc.updateLines(lines)
        }

        player.sendMessage(" ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${from.getName()} ${ChatColor.GRAY}-> ${to.getName()}${ChatColor.GRAY})")

        val lines = TextSplitter.split(length = 52, text = message, linePrefix = " ${ChatColor.GRAY}")
        for (line in lines) {
            player.sendMessage(line)
        }
    }

}
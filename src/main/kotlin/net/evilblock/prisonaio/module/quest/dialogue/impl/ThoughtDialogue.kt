/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ThoughtDialogue(private val message: String) : Dialogue() {

    override fun send(player: Player) {
        player.sendMessage(" ${ChatColor.GRAY}You think to yourself: $message")
    }

}
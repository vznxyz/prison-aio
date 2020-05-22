package net.evilblock.prisonaio.module.quest.dialogue.impl

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ThoughtDialogue(private val message: String) : Dialogue() {

    override fun send(player: Player) {
        player.sendMessage(" ${ChatColor.GRAY}You think to yourself: $message")
    }

}
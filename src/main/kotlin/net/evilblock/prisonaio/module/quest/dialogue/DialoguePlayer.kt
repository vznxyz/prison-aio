/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue

import mkremins.fanciful.FancyMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class DialoguePlayer(player: Player, sequence: DialogueSequence) {

    private var dialogue: LinkedList<Dialogue> = sequence.getSequence(player)
    private var dialoguesSent: Int = 0
    private var lastSent = System.currentTimeMillis()

    fun hasNext(): Boolean {
        return dialoguesSent < dialogue.size
    }

    fun getLast(): Dialogue {
        return dialogue.last
    }

    fun isReady(player: Player): Boolean {
        if (!hasNext()) {
            throw IllegalStateException("No next dialogue in sequence")
        }

        if (dialoguesSent > 0 && dialogue[dialoguesSent - 1].useState) {
            return dialogue[dialoguesSent - 1].complete
        }

        return dialogue[dialoguesSent].canSend(player)
    }

    fun send(player: Player) {
        if (!hasNext()) {
            throw IllegalStateException("No next dialogue in sequence")
        }

        val next = dialogue[dialoguesSent++]
        lastSent = System.currentTimeMillis()

        if (dialoguesSent == 1) {
            player.sendMessage("")

            FancyMessage(" ")
                .then("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Dialogue Start${ChatColor.GRAY}]")
                .then(" ")
                .then("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Skip${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to skip the dialogue."))
                .command("/quest dialogue skip")
                .send(player)

            player.sendMessage("")
        }

        next.send(player)

        if (hasNext()) {
            if (next.isSpaced()) {
                player.sendMessage("")
            }
        } else {
            player.sendMessage("")
            player.sendMessage(" ${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Dialogue Finish${ChatColor.GRAY}]")
            player.sendMessage("")
        }
    }

    fun isSleeping(): Boolean {
        return System.currentTimeMillis() < lastSent + dialogue[dialoguesSent].delay
    }

    fun getDialoguesForSkip(): List<Dialogue> {
        return dialogue.filter { !it.canBeSkipped() && dialogue.indexOf(it) > dialoguesSent }
    }

    fun canAllBeSent(player: Player): Boolean {
        for (dialogue in dialogue) {
            if (!dialogue.canSend(player)) {
                return false
            }
        }
        return true
    }

}
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
    private var sentIndex: Int = -1
    private var lastSent = System.currentTimeMillis()

    fun isOnCooldown(): Boolean {
        if (sentIndex == -1) {
            return false
        }

        val lastDialogue = dialogue[sentIndex]
        return System.currentTimeMillis() - lastSent < lastDialogue.delay
    }

    fun hasNext(): Boolean {
        return sentIndex + 1 < dialogue.size
    }

    fun canSendNext(player: Player): Boolean {
        if (!hasNext()) {
            throw IllegalStateException("No next dialogue in sequence")
        }

        return dialogue[sentIndex + 1].canSend(player)
    }

    fun sendNext(player: Player) {
        if (!hasNext()) {
            throw IllegalStateException("No next dialogue in sequence")
        }

        val next = dialogue[++sentIndex]
        lastSent = System.currentTimeMillis()

        if (sentIndex == 0) {
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

    fun getDialoguesForSkip(): List<Dialogue> {
        return dialogue.filter { !it.canBeSkipped() && dialogue.indexOf(it) > sentIndex }
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
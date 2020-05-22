package net.evilblock.prisonaio.module.quest.dialogue

import org.bukkit.entity.Player
import java.util.*

interface DialogueSequence {

    fun getSequence(player: Player): LinkedList<Dialogue>

}
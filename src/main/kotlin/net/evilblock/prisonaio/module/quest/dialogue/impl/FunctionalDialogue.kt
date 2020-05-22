package net.evilblock.prisonaio.module.quest.dialogue.impl

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import org.bukkit.entity.Player

open class FunctionalDialogue(private val function: (Player) -> Unit, delay: Long = 1000L) : Dialogue(delay = delay) {

    override fun send(player: Player) {
        function.invoke(player)
    }

    override fun canBeSkipped(): Boolean {
        return false
    }

}
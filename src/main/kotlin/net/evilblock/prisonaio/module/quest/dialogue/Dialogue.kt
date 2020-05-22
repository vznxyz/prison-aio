package net.evilblock.prisonaio.module.quest.dialogue

import org.bukkit.entity.Player

abstract class Dialogue(val delay: Long = 3000L) {

    abstract fun send(player: Player)

    open fun canSend(player: Player): Boolean {
        return true
    }

    open fun isSpaced(): Boolean {
        return true
    }

    open fun canBeSkipped(): Boolean {
        return true
    }

}
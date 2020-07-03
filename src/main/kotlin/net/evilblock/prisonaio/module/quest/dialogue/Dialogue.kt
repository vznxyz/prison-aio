/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
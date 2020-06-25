/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue

import org.bukkit.entity.Player
import java.util.*

interface DialogueSequence {

    fun getSequence(player: Player): LinkedList<Dialogue>

}
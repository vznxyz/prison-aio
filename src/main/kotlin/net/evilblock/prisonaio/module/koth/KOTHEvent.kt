/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.koth

import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class KOTHEvent(val region: KOTH, var captureTime: Long) {

    var active: Boolean = false

    val capturing: MutableSet<Player> = ConcurrentHashMap.newKeySet()

    var capturer: Player? = null
    var captureStartedAt: Long? = null

    val startedAt: Long = System.currentTimeMillis()

    fun hasCaptured(): Boolean {
        return capturer != null
                && captureStartedAt != null
                && (System.currentTimeMillis() - captureStartedAt!! >= captureTime)
    }

}
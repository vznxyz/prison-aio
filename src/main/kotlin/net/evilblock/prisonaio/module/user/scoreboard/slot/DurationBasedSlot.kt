/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.slot

import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardSlot

abstract class DurationBasedSlot(val duration: Long) : ScoreboardSlot() {

    val createdAt: Long = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= createdAt + duration
    }

}
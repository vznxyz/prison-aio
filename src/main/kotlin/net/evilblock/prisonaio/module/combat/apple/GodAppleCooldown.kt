/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.apple

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class GodAppleCooldown(val uuid: UUID) {

    private var expiresAt: Long = System.currentTimeMillis() + COOLDOWN

    fun reset() {
        expiresAt = System.currentTimeMillis() + COOLDOWN
    }

    fun getRemainingTime(): Long {
        return expiresAt - System.currentTimeMillis()
    }

    fun getRemainingSeconds(): Double {
        return (10.0 * getRemainingTime() / 1000.0).roundToInt() / 10.0
    }

    fun hasExpired(): Boolean {
        return getRemainingTime() <= 0
    }

    companion object {
        private val COOLDOWN = TimeUnit.MINUTES.toMillis(5L)
    }

}
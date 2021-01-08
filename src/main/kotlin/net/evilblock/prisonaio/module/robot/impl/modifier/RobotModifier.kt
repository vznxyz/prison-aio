/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.impl.modifier

import net.evilblock.cubed.util.Duration

class RobotModifier(
    val type: RobotModifierType,
    val value: Double,
    val duration: Duration?
) {

    val startedAt: Long = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= startedAt + (duration?.get() ?: 0L)
    }

    fun isPermanent(): Boolean {
        return !type.durationBased || duration != null
    }

    fun getRemainingTime(): Long {
        return (startedAt + duration!!.get()) - System.currentTimeMillis()
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.perk

import com.google.gson.JsonObject
import java.time.Instant
import java.util.*

open class PerkGrant(
    val perk: Perk,
    var issuedBy: UUID? = null,
    private var duration: Long,
    private var reason: String
) {

    var issuedAt: Date = Date.from(Instant.now())
    private var startAt: Long = System.currentTimeMillis()
    private var paused: Boolean = false
    private var pausedAt: Long = -1
    val metadata: JsonObject = JsonObject()

    fun isPaused(): Boolean {
        return paused
    }

    fun pause() {
        paused = true
        pausedAt = System.currentTimeMillis()
    }

    fun start() {
        paused = false
        pausedAt = -1
        startAt = System.currentTimeMillis()
    }

    fun isExpired(): Boolean {
        return !isPermanent() && getRemainingTime() <= 0
    }

    fun isPermanent(): Boolean {
        return duration == Long.MAX_VALUE
    }

    fun getRemainingTime(): Long {
        return if (paused) {
            pausedAt - (startAt + duration)
        } else {
            (startAt + duration) - System.currentTimeMillis()
        }
    }

    fun getReason(): String {
        return reason
    }

}
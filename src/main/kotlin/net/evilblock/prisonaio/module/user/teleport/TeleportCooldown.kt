/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.teleport

class TeleportCooldown(val duration: Long, val call: () -> Unit) {

    val createdAt: Long = System.currentTimeMillis()

    fun hasExpired(): Boolean {
        return System.currentTimeMillis() >= createdAt + duration
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.teleport

import org.bukkit.Location

class UserTeleport(
    val name: String,
    val location: Location,
    val duration: Long,
    val call: (Boolean) -> Unit
) {

    val createdAt: Long = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= createdAt + duration
    }

    fun getRemainingTime(): Long {
        return (createdAt + duration) - System.currentTimeMillis()
    }

    fun getRemainingSeconds(): Int {
        return (getRemainingTime() / 1000.0).toInt()
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.notification

data class AHNotification(
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
) {

    var read: Boolean = false
    var readAt: Long? = null

    fun needsDeletion(): Boolean {
        return read && readAt != null && System.currentTimeMillis() >= readAt!! + 30_000L
    }

}
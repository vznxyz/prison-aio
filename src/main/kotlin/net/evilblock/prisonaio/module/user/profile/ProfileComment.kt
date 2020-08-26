/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.profile

import java.util.*

data class ProfileComment(
    val creator: UUID,
    val message: String
) {

    val createdAt: Long = System.currentTimeMillis()
    var read: Boolean = false

}
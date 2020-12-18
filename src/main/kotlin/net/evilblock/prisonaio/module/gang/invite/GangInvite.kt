/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.invite

import net.evilblock.prisonaio.module.gang.GangHandler
import java.util.*

data class GangInvite(val invitedBy: UUID, val invitedAt: Long) {

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= invitedAt + GangHandler.INVITE_EXPIRE_TIME
    }

    fun getRemainingTime(): Long {
        return (invitedAt + GangHandler.INVITE_EXPIRE_TIME) - System.currentTimeMillis()
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.service

import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.service.Service

object GangInvitesExpiryService : Service {

    override fun run() {
        for (gang in GangHandler.getAllGangs()) {
            gang.expireInvitations()
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger.service

import net.evilblock.prisonaio.module.combat.logger.CombatLogger
import net.evilblock.prisonaio.module.combat.logger.CombatLoggerHandler
import net.evilblock.prisonaio.module.combat.logger.event.CombatLoggerExpireEvent
import net.evilblock.prisonaio.service.Service

object CombatLoggerExpiryService : Service {

    override fun run() {
        val expired = arrayListOf<CombatLogger>()

        for (logger in CombatLoggerHandler.getLoggers()) {
            if (logger.isExpired()) {
                expired.add(logger)
            }
        }

        for (logger in expired) {
            try {
                CombatLoggerExpireEvent(logger).call()
                logger.expire()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger

import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.combat.CombatModule
import java.util.*

object CombatLoggerHandler : PluginHandler {

    private val loggers: MutableMap<UUID, CombatLogger> = hashMapOf()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    fun trackLogger(logger: CombatLogger) {
        loggers[logger.uuid] = logger
    }

    fun forgetLogger(logger: CombatLogger) {
        loggers.remove(logger.uuid)
    }

}
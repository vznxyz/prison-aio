/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger

import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.combat.CombatModule
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CombatLoggerHandler : PluginHandler() {

    private val loggers: MutableMap<UUID, CombatLogger> = ConcurrentHashMap()
    private val loggersByOwner: MutableMap<UUID, CombatLogger> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    fun getLoggerById(uuid: UUID): CombatLogger? {
        return loggers[uuid]
    }

    fun getLoggerByOwner(owner: UUID): CombatLogger? {
        return loggersByOwner[owner]
    }

    fun trackLogger(logger: CombatLogger) {
        loggers[logger.uuid] = logger
        loggersByOwner[logger.uuid] = logger

        EntityManager.trackEntity(logger)
    }

    fun forgetLogger(logger: CombatLogger) {
        loggers.remove(logger.uuid)
        loggersByOwner.remove(logger.uuid)

        EntityManager.forgetEntity(logger)
    }

}
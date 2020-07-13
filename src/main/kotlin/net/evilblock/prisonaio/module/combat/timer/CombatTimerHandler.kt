/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.timer

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.combat.CombatModule
import java.util.*

object CombatTimerHandler : PluginHandler {

    private val timers: MutableMap<UUID, CombatTimer> = hashMapOf()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    fun getTimer(uuid: UUID): CombatTimer? {
        return timers[uuid]
    }

    fun trackTimer(timer: CombatTimer) {
        timers[timer.uuid] = timer
    }

    fun forgetTimer(timer: CombatTimer) {
        timers.remove(timer.uuid)
    }

}
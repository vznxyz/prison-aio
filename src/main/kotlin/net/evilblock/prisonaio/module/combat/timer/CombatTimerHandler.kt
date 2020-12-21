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
import org.bukkit.entity.Player
import java.util.*

object CombatTimerHandler : PluginHandler() {

    private val timers: MutableMap<UUID, CombatTimer> = hashMapOf()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    @JvmStatic
    fun getTimer(uuid: UUID): CombatTimer? {
        return timers[uuid]
    }

    @JvmStatic
    fun isOnTimer(player: Player): Boolean {
        val timer = getTimer(player.uniqueId)
        return timer != null && !timer.hasExpired()
    }

    @JvmStatic
    fun trackTimer(timer: CombatTimer) {
        timers[timer.uuid] = timer
    }

    @JvmStatic
    fun forgetTimer(timer: CombatTimer) {
        timers.remove(timer.uuid)
    }

}
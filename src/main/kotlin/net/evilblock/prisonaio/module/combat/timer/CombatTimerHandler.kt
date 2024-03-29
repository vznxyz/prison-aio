/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.timer

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.combat.CombatModule
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CombatTimerHandler : PluginHandler() {

    private val timers: MutableMap<UUID, CombatTimer> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    override fun initialLoad() {
        super.initialLoad()

        Tasks.async {
            val toRemove = arrayListOf<UUID>()

            for (timer in timers.values) {
                if (timer.hasExpired()) {
                    toRemove.add(timer.uuid)
                }
            }

            for (uuid in toRemove) {
                timers.remove(uuid)
            }
        }
    }

    fun getTimer(uuid: UUID): CombatTimer? {
        return timers[uuid]
    }

    fun getTimer(player: Player): CombatTimer? {
        return getTimer(player.uniqueId)
    }

    fun isOnTimer(player: Player): Boolean {
        val timer = getTimer(player.uniqueId)
        return timer != null && !timer.hasExpired()
    }

    fun trackTimer(timer: CombatTimer) {
        timers[timer.uuid] = timer
    }

    fun forgetTimer(timer: CombatTimer) {
        timers.remove(timer.uuid)
    }

    @JvmStatic
    fun resetTimer(player: Player) {
        val attackerTimer = getTimer(player.uniqueId)
        if (attackerTimer == null) {
            trackTimer(CombatTimer(player.uniqueId))
        } else {
            attackerTimer.reset()
        }
    }

}
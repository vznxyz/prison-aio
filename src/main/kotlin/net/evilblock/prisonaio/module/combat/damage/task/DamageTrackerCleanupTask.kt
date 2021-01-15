/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.damage.task

import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import java.util.*

object DamageTrackerCleanupTask : Runnable {

    override fun run() {
        val toRemove = arrayListOf<UUID>()
        for ((uuid, data) in DamageTracker.lastKilled.entries) {
            if (System.currentTimeMillis() >= data.second + 180_000L) {
                toRemove.add(uuid)
            }
        }

        for (uuid in toRemove) {
            DamageTracker.lastKilled.remove(uuid)
        }

        toRemove.clear()

        for ((uuid, data) in DamageTracker.boosting) {
            if (System.currentTimeMillis() >= data.second + 3000_000L) {
                toRemove.add(uuid)
            }
        }

        for (uuid in toRemove) {
            DamageTracker.boosting.remove(uuid)
        }
    }

}
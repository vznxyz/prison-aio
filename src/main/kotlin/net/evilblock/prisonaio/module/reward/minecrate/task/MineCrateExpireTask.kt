/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.minecrate.task

import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler

object MineCrateExpireTask : Runnable {

    override fun run() {
        for (spawnedCrate in MineCrateHandler.getSpawnedCrates()) {
            if (System.currentTimeMillis() - spawnedCrate.spawnedAt >= 60_000L) {
                spawnedCrate.destroy()
                MineCrateHandler.forgetSpawnedCrate(spawnedCrate)
            }
        }
    }

}
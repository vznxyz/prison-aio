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
package net.evilblock.prisonaio.module.mine.task

import net.evilblock.prisonaio.module.mine.MineHandler

object MineResetTask : Runnable {

    private val secondMarkers = listOf(1, 2, 3, 4, 5, 15, 30, 60, 120, 180, 240, 300, 900, 1800, 3600)

    override fun run() {
        for (mine in MineHandler.getMines()) {
            if (mine.region == null) {
                continue
            }

            if (mine.getRemainingPercentage() < 20.0) {
                if (mine.nextReset > 5) {
                    mine.nextReset = 5
                }
            }

            mine.nextReset--

            // check if we need to send an interval message for this tick
            if (secondMarkers.contains(mine.nextReset)) {
                mine.resetConfig.sendIntervalMessage(mine, mine.nextReset)
            }

            // check if this mine is due for a reset
            if (mine.nextReset <= 0) {
                try {
                    mine.resetRegion()

                    // send reset message
                    mine.resetConfig.sendResetMessage(mine)

                    // teleport players inside the mine to the spawn point
                    if (mine.spawnPoint != null) {
                        mine.getNearbyPlayers().forEach { it.teleport(mine.spawnPoint) }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

}
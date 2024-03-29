/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.service

import net.evilblock.cubed.lite.LiteEdit
import net.evilblock.cubed.lite.LiteRegion
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.variant.normal.NormalMine
import net.evilblock.prisonaio.service.Service
import java.util.concurrent.atomic.AtomicInteger

object MineResetService : Service {

    private val nextResetCountdown = hashMapOf<NormalMine, AtomicInteger>()

    override fun run() {
        for (mine in MineHandler.getMines()) {
            if (mine !is NormalMine) {
                continue
            }

            if (!mine.supportsAutomaticReset()) {
                continue
            }

            if (mine.region == null) {
                continue
            }

            if (mine.blocksConfig.blockTypes.isEmpty()) {
                continue
            }

            if (System.currentTimeMillis() - mine.lastResetCheck >= 30_000L) {
                mine.lastResetCheck = System.currentTimeMillis()

                val liteRegion = LiteRegion(mine.region!!)
                LiteEdit.countAir(liteRegion, object : LiteEdit.CountAirCallback {
                    override fun callback(total: Int, air: Int, skipped: Int) {
                        if (skipped != 0) {
                            return
                        }

                        val progress = air.toFloat() / total.toFloat()
                        if (progress > 0.4F) {
                            nextResetCountdown[mine] = AtomicInteger(6)
                        }
                    }
                })
            }
        }

        val toRemove = arrayListOf<Mine>()
        for ((mine, seconds) in nextResetCountdown) {
            if (seconds.decrementAndGet() > 0) {
                mine.resetConfig.sendIntervalMessage(mine, seconds.get())
            } else {
                mine.resetRegion()
                mine.resetConfig.sendResetMessage(mine)

                if (mine.spawnPoint != null) {
                    mine.getNearbyPlayers().forEach {
                        it.teleport(mine.spawnPoint)
                    }
                }

                toRemove.add(mine)
            }
        }

        for (mine in toRemove) {
            nextResetCountdown.remove(mine)
        }
    }

}
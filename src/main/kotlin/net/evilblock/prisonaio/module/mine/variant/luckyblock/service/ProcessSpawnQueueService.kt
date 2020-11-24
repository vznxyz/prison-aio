/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.service

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockMine
import net.evilblock.prisonaio.service.Service

object ProcessSpawnQueueService : Service {

    override fun run() {
        if (!MinesModule.loaded) {
            return
        }

        if (LuckyBlockHandler.disabled) {
            return
        }

        for (mine in MineHandler.getMines()) {
            if (mine is LuckyBlockMine) {
                if (mine.spawnLocations.isEmpty()) {
                    continue
                }

                var spawns = 0

                if (mine.spawnQueue.isEmpty()) {
                    if (mine.spawnedEntities.size < MinesModule.getLuckyBlockMineMaxSpawns()) {
                        spawns = MinesModule.getLuckyBlockMineMaxSpawns() - mine.spawnedEntities.size
                    }
                } else {
                    val nextSpawn = mine.spawnQueue.peekFirst() + MinesModule.getLuckyBlockRegenTime()
                    if (System.currentTimeMillis() >= nextSpawn) {
                        if (mine.spawnedEntities.size + spawns < MinesModule.getLuckyBlockMineMaxSpawns()) {
                            mine.spawnQueue.pollFirst()
                            spawns++
                        }
                    }
                }

                if (spawns > 0) {
                    Tasks.sync {
                        for (i in 0 until spawns) {
                            mine.spawnBlock()
                        }
                    }
                }
            }
        }
    }

}
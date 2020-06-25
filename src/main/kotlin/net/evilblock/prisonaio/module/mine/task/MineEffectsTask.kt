/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.task

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.Bukkit

object MineEffectsTask : Runnable {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val nearbyMine = MineHandler.getNearbyMine(player)
            if (nearbyMine.isPresent) {
                PrisonAIO.instance.server.scheduler.runTask(PrisonAIO.instance) {
                    nearbyMine.get().effectsConfig.giveEffectsToPlayer(player)
                }
            }
        }
    }

}
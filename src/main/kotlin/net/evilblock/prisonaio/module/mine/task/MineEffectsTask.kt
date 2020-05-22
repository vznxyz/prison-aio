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
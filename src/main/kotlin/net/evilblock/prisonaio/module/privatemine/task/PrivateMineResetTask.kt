package net.evilblock.prisonaio.module.privatemine.task

import com.boydti.fawe.util.TaskManager
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.Bukkit

/**
 * Must be ran asynchronously.
 */
object PrivateMineResetTask : Runnable {

    override fun run() {
        for (mine in PrivateMineHandler.getAllMines()) {
            // check if reset interval has passed
            if (System.currentTimeMillis() - mine.lastReset > mine.tier.resetInterval) {
                // perform the reset
                mine.resetMineArea()

                // send gains notification to mine owner
                Bukkit.getPlayer(mine.owner)?.sendMessage(PrivateMinesModule.getNotificationLines("mine-gains").map { mine.translateVariables(it) }.toTypedArray())

                // reset gains
                mine.moneyGained = 0L

                // teleport everyone in that mine to the spawn point
                for (activePlayer in mine.getActivePlayers()) {
                    TaskManager.IMP.sync {
                        activePlayer.teleport(mine.spawnPoint)

                        // send mine reset notification
                        PrivateMinesModule.getNotificationLines("mine-reset").forEach { activePlayer.sendMessage(mine.translateVariables(it)) }
                    }
                }
            }
        }
    }

}
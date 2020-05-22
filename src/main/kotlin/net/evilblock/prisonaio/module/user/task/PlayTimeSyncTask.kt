package net.evilblock.prisonaio.module.user.task

import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit

object PlayTimeSyncTask : Runnable {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val user = UserHandler.getUser(player.uniqueId)
            user.statistics.syncPlayTime()
        }
    }

}
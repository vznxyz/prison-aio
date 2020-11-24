/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.service

import net.evilblock.cubed.lite.LiteEdit
import net.evilblock.cubed.lite.LiteRegion
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.privatemine.PrivateMine
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.ChatColor
import java.util.concurrent.atomic.AtomicInteger

object ResetMineService : Service {

    private val nextResetCountdown = hashMapOf<PrivateMine, AtomicInteger>()

    override fun run() {
        for (mine in PrivateMineHandler.getAllMines()) {
            if (System.currentTimeMillis() - mine.lastResetCheck >= 30_000L) {
                mine.lastResetCheck = System.currentTimeMillis()

                val liteRegion = LiteRegion(mine.innerCuboid)
                LiteEdit.countAir(liteRegion, object : LiteEdit.CountAirCallback {
                    override fun callback(total: Int, air: Int, skipped: Int) {
                        if (skipped != 0) {
                            return
                        }

                        val progress = air.toFloat() / total.toFloat()
                        if (progress > 0.5F) {
                            nextResetCountdown[mine] = AtomicInteger(6)
                        }
                    }
                })
            }
        }

        val toRemove = arrayListOf<PrivateMine>()
        for ((mine, seconds) in nextResetCountdown) {
            if (seconds.decrementAndGet() > 0) {
                sendIntervalMessage(mine, seconds.get())
            } else {
                mine.resetRegion()

                mine.getActivePlayers().forEach {
                    it.teleport(mine.spawnPoint)
                }

                toRemove.add(mine)
            }
        }

        for (mine in toRemove) {
            nextResetCountdown.remove(mine)
        }
    }

    private fun sendIntervalMessage(mine: PrivateMine, secondsRemaining: Int) {
        val message = "${ChatColor.GRAY}This ${ChatColor.RED}${ChatColor.BOLD}Private Mine ${ChatColor.GRAY}is resetting in ${TimeUtil.formatIntoDetailedString(secondsRemaining)}!"
        mine.getActivePlayers().forEach { it.sendMessage(message) }
    }

}
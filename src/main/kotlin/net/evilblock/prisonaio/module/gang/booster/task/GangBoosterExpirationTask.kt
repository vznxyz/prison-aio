/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.booster.task

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor

object GangBoosterExpirationTask : Runnable {

    override fun run() {
        for (gang in GangHandler.getAllGangs()) {
            if (gang.getBoosters().isNotEmpty()) {
                val iterator = gang.getBoosters().iterator()
                while (iterator.hasNext()) {
                    val booster = iterator.next()
                    if (System.currentTimeMillis() >= booster.expiration) {
                        iterator.remove()

                        gang.sendMessagesToMembers(
                            "",
                            " ${ChatColor.RED}${ChatColor.BOLD}${booster.boosterType.rendered} Booster Deactivated",
                            " ${ChatColor.GRAY}The booster, purchased by ${Cubed.instance.uuidCache.name(booster.purchasedBy)}, has expired.",
                            ""
                        )
                    }
                }
            }
        }
    }

}
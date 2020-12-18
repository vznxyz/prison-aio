/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.service

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.ChatColor

object AutoSaveListingsService : Service {

    override fun run() {
        var saved = 0
        var failed = 0

        for (listing in AuctionHouseHandler.getAllListings()) {
            if (listing.requiresSave) {
                listing.requiresSave = false

                try {
                    AuctionHouseHandler.saveListing(listing)
                    saved++
                } catch (e: Exception) {
                    e.printStackTrace()
                    failed++
                }
            }
        }

        if (saved != 0 || failed != 0) {
            PrisonAIO.instance.systemLog("${ChatColor.GRAY}Auto-saved ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(saved)} ${ChatColor.GRAY}GE listings (${ChatColor.RED}${NumberUtils.format(failed)} failed${ChatColor.GRAY})")
        }
    }

}
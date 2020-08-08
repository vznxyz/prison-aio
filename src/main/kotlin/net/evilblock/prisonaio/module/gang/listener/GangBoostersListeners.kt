/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangModule
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object GangBoostersListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        val assumedGang = GangHandler.getAssumedGang(event.player.uniqueId)
        if (assumedGang != null) {
            if (assumedGang.hasBooster(GangBooster.BoosterType.SALES_MULTIPLIER)) {
                event.multiplier += GangModule.readSalesMultiplierMod()
            }
        }
    }

}
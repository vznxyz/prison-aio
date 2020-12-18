/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMine
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.event.DetermineShopEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object PrivateMineShopListeners : Listener {

    /**
     * Taxes any shop sales, where the taxes are given to the mine owner.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSellAllEvent(event: PlayerSellToShopEvent) {
        val currentMine = PrivateMineHandler.getCurrentMine(event.player) ?: return

        // ignore if the player owns the mine
        if (event.player.uniqueId != currentMine.owner) {
            if (currentMine.salesTax.coerceAtLeast(1.0) != 1.0) {
                val taxedMoney = event.getCost().toDouble() / currentMine.salesTax

                currentMine.moneyGained += taxedMoney.toLong()

                val taxedUser = UserHandler.getUser(event.player.uniqueId)
                taxedUser.subtractMoneyBalance(taxedMoney)
                taxedUser.requiresSave()

                Tasks.async {
                    val owningUser = UserHandler.getOrLoadAndCacheUser(currentMine.owner)
                    owningUser.addMoneyBalance(taxedMoney)
                    owningUser.requiresSave()
                }
            }
        }
    }

    /**
     * Redirects items sold in PrivateMines to the PrivateMines shops.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onDetermineShopEvent(event: DetermineShopEvent) {
        val currentMine = PrivateMineHandler.getCurrentMine(event.player)
        val region = RegionHandler.findRegion(event.player)

        val shop = ShopHandler.getShopById("PMine")
        if (shop.isPresent && (currentMine != null || region is PrivateMine)) {
            event.shop = shop.get()
        }
    }

}
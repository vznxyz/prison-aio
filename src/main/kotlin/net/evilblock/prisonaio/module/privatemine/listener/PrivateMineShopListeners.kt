/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.listener

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.event.DetermineShopEvent
import org.bukkit.Bukkit
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
            val mineOwner = Bukkit.getOfflinePlayer(currentMine.owner)

            if (currentMine.salesTax.coerceAtLeast(0.0) != 0.0) {
                val taxedMoney = event.getSellCost().toDouble() / currentMine.salesTax

                currentMine.moneyGained += taxedMoney.toLong()

                VaultHook.useEconomy { economy ->
                    economy.withdrawPlayer(event.player, taxedMoney)
                    economy.depositPlayer(mineOwner, taxedMoney)
                }
            }
        }
    }

    /**
     * Redirects items sold in PrivateMines to the PrivateMines shops.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onDetermineShopEvent(event: DetermineShopEvent) {
        val currentMine = PrivateMineHandler.getCurrentMine(event.player) ?: return
        val shop = ShopHandler.getShopById("PMine-${currentMine.tier.number}")
        if (shop.isPresent) {
            event.shop = shop.get()
        }
    }

}
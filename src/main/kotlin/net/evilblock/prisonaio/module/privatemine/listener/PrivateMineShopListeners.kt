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
            val salesTax = event.getSellCost() / currentMine.salesTax

            currentMine.moneyGained += salesTax.toLong()

            VaultHook.useEconomy { economy ->
                economy.withdrawPlayer(event.player, salesTax)
                economy.depositPlayer(mineOwner, salesTax)
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
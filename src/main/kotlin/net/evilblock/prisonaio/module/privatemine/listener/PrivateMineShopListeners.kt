package net.evilblock.prisonaio.module.privatemine.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object PrivateMineShopListeners : Listener {

    /**
     * A few things happen in this event handler:
     *  - Prevents any sales to PrivateMines shops while not in a mine.
     *  - Taxes any shop sales, where the taxes are given to the mine owner.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSellAllEvent(event: PlayerSellToShopEvent) {
        val currentMine = PrivateMineHandler.getCurrentMine(event.player)

        // prevent selling to PrivateMines shops if not in a mine
        if (event.shop.name.startsWith(PrivateMineTier.SHOP_NAME_PREFIX)) {
            if (currentMine == null) {
                event.player.sendMessage("${ChatColor.RED}You can't sell to a Private Mines shop without being in a mine.")
                event.isCancelled = true
                return
            }

            val tierNumber = event.shop.name.split("Tier")[1].toInt()
            if (currentMine.tier.number != tierNumber) {
                event.player.sendMessage("${ChatColor.RED}You can't sell to that tier shop while in a different tier mine.")
                event.isCancelled = true
                return
            }
        }

        // check if the player is in a mine
        if (currentMine != null) {
            // ignore if the player owns the mine
            if (event.player.uniqueId == currentMine.owner) {
                return
            }

            val mineOwner = Bukkit.getOfflinePlayer(currentMine.owner)
            val salesTax = event.getSellCost() / currentMine.salesTax

            currentMine.moneyGained += salesTax.toLong()

            Tasks.delayed(2L) {
                VaultHook.useEconomy { economy ->
                    economy.withdrawPlayer(event.player, salesTax)
                    economy.depositPlayer(mineOwner, salesTax)
                }
            }
        }
    }

}
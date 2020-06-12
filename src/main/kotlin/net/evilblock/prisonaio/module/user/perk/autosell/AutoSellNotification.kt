package net.evilblock.prisonaio.module.user.perk.autosell

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.util.Formats
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object AutoSellNotification : Listener {

    private val trackedGains = hashMapOf<UUID, Double>()

    init {
        Tasks.asyncTimer(20L * 60L, 20L * 60L) {
            for (player in Bukkit.getOnlinePlayers()) {
                val gains = trackedGains.getOrDefault(player.uniqueId, 0.0)
                if (gains > 0.0) {
                    player.sendMessage("")
                    player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}AutoSell Gains")
                    player.sendMessage(" ${ChatColor.GRAY}You've made ${Formats.formatMoney(gains)} ${ChatColor.GRAY}in the last 60 seconds!")
                    player.sendMessage("")

                    trackedGains.remove(player.uniqueId)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerSellToShopEvent(event: PlayerSellToShopEvent) {
        if (event.autoSell) {
            trackedGains[event.player.uniqueId] = trackedGains.getOrDefault(event.player.uniqueId, 0.0) + event.getSellCost()
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        trackedGains.remove(event.player.uniqueId)
    }

}
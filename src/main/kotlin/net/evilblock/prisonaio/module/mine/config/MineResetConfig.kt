package net.evilblock.prisonaio.module.mine.config

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.mine.Mine
import org.bukkit.Bukkit
import org.bukkit.ChatColor

data class MineResetConfig(
    /**
     * The amount of time in seconds between mine resets
     */
    var resetInterval: Int = 60 * 5,
    /**
     * If reset messages should be sent to players near the mine
     */
    var resetMessagesEnabled: Boolean = true,
    /**
     * If reset messages should be broadcast to all players or just the players near the mine
     */
    var broadcastResetMessages: Boolean = false,
    /**
     * If interval messages should be sent to players near the mine
     */
    var intervalMessagesEnabled: Boolean = true
) {

    fun sendResetMessage(mine: Mine) {
        val sendTo = if (broadcastResetMessages) {
            Bukkit.getOnlinePlayers()
        } else {
            mine.getNearbyPlayers()
        }

        val message = "${ChatColor.GRAY}Mine ${ChatColor.RED}${ChatColor.BOLD}${mine.id} ${ChatColor.GRAY}has reset!"
        sendTo.forEach { it.sendMessage(message) }
    }

    fun sendIntervalMessage(mine: Mine, secondsRemaining: Int) {
        val sendTo = if (broadcastResetMessages) {
            Bukkit.getOnlinePlayers()
        } else {
            mine.getNearbyPlayers()
        }

        val message = "${ChatColor.GRAY}Mine ${ChatColor.RED}${ChatColor.BOLD}${mine.id} ${ChatColor.GRAY}is resetting in ${TimeUtil.formatIntoDetailedString(secondsRemaining)}!"
        sendTo.forEach { it.sendMessage(message) }
    }

}
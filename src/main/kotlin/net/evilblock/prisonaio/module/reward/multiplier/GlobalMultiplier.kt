/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier

import net.evilblock.cubed.util.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.text.DecimalFormat

data class GlobalMultiplier(val type: GlobalMultiplierType, val multiplier: Double, val expires: Long) {

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expires
    }

    fun getRemainingTime(): String {
        val remainingSeconds = ((expires - System.currentTimeMillis()) / 1000.0).toInt()
        return TimeUtil.formatIntoDetailedString(remainingSeconds)
    }

    fun start() {
        val lines = arrayListOf<String>().also { lines ->
            lines.add("")
            lines.add(" ${ChatColor.GREEN}${ChatColor.BOLD}${type.getFormattedName()} Multiplier Activated ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}X${ChatColor.GRAY})")
            lines.add(" ${ChatColor.GRAY}A global multiplier has been activated!")
            lines.add("")
            lines.add(" ${ChatColor.GRAY}${ChatColor.ITALIC}Expires in ${ChatColor.GREEN}${ChatColor.ITALIC}${getRemainingTime()}${ChatColor.GRAY}${ChatColor.ITALIC}!")
            lines.add("")
        }

        for (player in Bukkit.getOnlinePlayers()) {
            for (line in lines) {
                player.sendMessage(line)
            }
        }
    }

    fun end(forced: Boolean) {
        val lines = arrayListOf<String>().also { lines ->
            if (forced) {
                lines.add("")
                lines.add(" ${ChatColor.DARK_RED}${ChatColor.BOLD}${type.getFormattedName()} Multiplier Deactivated ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}X${ChatColor.GRAY})")
                lines.add(" ${ChatColor.GRAY}The global multiplier has been deactivated!")
                lines.add("")
            } else {
                lines.add("")
                lines.add(" ${ChatColor.DARK_RED}${ChatColor.BOLD}${type.getFormattedName()} Multiplier Expired ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}X${ChatColor.GRAY})")
                lines.add(" ${ChatColor.GRAY}The global multiplier has expired!")
                lines.add("")
            }
        }

        for (player in Bukkit.getOnlinePlayers()) {
            for (line in lines) {
                player.sendMessage(line)
            }
        }
    }

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("#.##")
    }

}
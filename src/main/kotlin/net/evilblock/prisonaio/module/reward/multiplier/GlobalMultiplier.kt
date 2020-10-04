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

data class GlobalMultiplier(val multiplier: Double, val expires: Long) {

    fun start() {
        val lines = arrayListOf<String>()

        lines.add("")
        lines.add(" ${ChatColor.GREEN}${ChatColor.BOLD}Global Multiplier Activated ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}X${ChatColor.GRAY})")
        lines.add(" ${ChatColor.GRAY}This global multiplier will stack with any active")
        lines.add(" ${ChatColor.GRAY}multipliers you already have!")
        lines.add("")
        lines.add(" ${ChatColor.GRAY}${ChatColor.ITALIC}Expires in ${ChatColor.GREEN}${ChatColor.ITALIC}${getRemainingTime()}${ChatColor.GRAY}${ChatColor.ITALIC}!")
        lines.add("")

        for (player in Bukkit.getOnlinePlayers()) {
            for (line in lines) {
                player.sendMessage(line)
            }
        }
    }

    fun end(forced: Boolean) {
        val lines = arrayListOf<String>()

        if (forced) {
            lines.add("")
            lines.add(" ${ChatColor.DARK_RED}${ChatColor.BOLD}Global Multiplier Deactivated ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}X${ChatColor.GRAY})")
            lines.add(" ${ChatColor.GRAY}An administrator has forcefully deactivated the")
            lines.add(" ${ChatColor.GRAY}global multiplier!")
            lines.add("")
        } else {
            lines.add("")
            lines.add(" ${ChatColor.DARK_RED}${ChatColor.BOLD}Global Multiplier Expired ${ChatColor.GRAY}(${ChatColor.GOLD}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}X${ChatColor.GRAY})")
            lines.add(" ${ChatColor.GRAY}The global multiplier has expired and will no longer")
            lines.add(" ${ChatColor.GRAY}stack with your multipliers!")
            lines.add("")
        }

        for (player in Bukkit.getOnlinePlayers()) {
            for (line in lines) {
                player.sendMessage(line)
            }
        }
    }

    fun getRemainingTime(): String {
        val remainingSeconds = ((expires - System.currentTimeMillis()) / 1000.0).toInt()
        return TimeUtil.formatIntoDetailedString(remainingSeconds)
    }

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("#.##")
    }

}
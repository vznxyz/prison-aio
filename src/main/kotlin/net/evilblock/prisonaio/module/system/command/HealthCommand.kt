/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.text.DecimalFormat

object HealthCommand {

    private val TPS_FORMAT = DecimalFormat("##.##")

    @Command(
        names = ["prison health"],
        description = "Prints information about the system",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        val onlinePlayers = Bukkit.getOnlinePlayers().size
        val offlineCachedUsers = UserHandler.getUsers().filter { it.cacheExpiry != null }.size

        sender.sendMessage("")
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}PRISON HEALTH")
        sender.sendMessage("${ChatColor.GRAY}TPS: ${ChatColor.RED}${ChatColor.BOLD}${TPS_FORMAT.format(MinecraftReflection.getTPS())} ${ChatColor.DARK_GRAY}/ ${ChatColor.GRAY}Memory: ${ChatColor.RED}${ChatColor.BOLD}${getMemoryUsage()} ${ChatColor.GRAY}used of ${ChatColor.RED}${ChatColor.BOLD}${getTotalMemory()}")
        sender.sendMessage("${ChatColor.GRAY}Online: ${ChatColor.RED}${ChatColor.BOLD}${onlinePlayers} ${ChatColor.DARK_GRAY}/ ${ChatColor.GRAY}Cached: ${ChatColor.RED}${ChatColor.BOLD}$offlineCachedUsers")
        sender.sendMessage("")
    }

    private fun getTotalMemory(): String {
        var memory = Runtime.getRuntime().totalMemory() / 1024
        var memoryUnit = 0

        while (memory > 999) {
            memory /= 1024
            memoryUnit++
        }

        return "$memory ${getMemoryUnitName(memoryUnit)}"
    }

    private fun getMemoryUsage(): String {
        var memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024
        var memoryUnit = 0

        while (memory > 999) {
            memory /= 1024
            memoryUnit++
        }

        return "$memory ${getMemoryUnitName(memoryUnit)}"
    }

    private fun getMemoryUnitName(unit: Int): String {
        return when (unit) {
            0 -> "KB"
            1 -> "MB"
            2 -> "GB"
            else -> "UNKNOWN UNIT"
        }
    }

}
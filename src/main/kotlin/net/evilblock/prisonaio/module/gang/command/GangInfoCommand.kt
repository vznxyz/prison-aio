/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.gang.Gang
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangInfoCommand {

    @Command(
        names = ["gang info", "gangs info", "gang who", "gangs who"],
        description = "Show information about a gang"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "gang", defaultValue = "self") gang: Gang) {
        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")

        val playersOnline = "${ChatColor.GREEN}${gang.getActiveMembers().size}${ChatColor.GRAY}/${gang.getMembers().size}"
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}${gang.name} ${ChatColor.GRAY}[$playersOnline]")

        val memberNames = gang.getMembers().map { uuid ->
            val username = Cubed.instance.uuidCache.name(uuid)

            val role = if (gang.isOwner(uuid)) {
                "**"
            } else {
                ""
            }

            return@map if (Bukkit.getPlayer(uuid) == null) {
                "${ChatColor.RED}$role$username"
            } else {
                "${ChatColor.GREEN}$role$username"
            }
        }

        sender.sendMessage("${ChatColor.GRAY} Announcement: ${ChatColor.LIGHT_PURPLE}${gang.announcement}")
        sender.sendMessage("${ChatColor.GRAY} Members: ${ChatColor.WHITE}${memberNames.joinToString(separator = "${ChatColor.WHITE}, ")}")
        sender.sendMessage("${ChatColor.GRAY} Value: ${ChatColor.RED}${NumberUtils.format(gang.cachedValue)}")

        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangMember
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

        val playersOnline = "${ChatColor.GREEN}${ChatColor.BOLD}${gang.getOnlineMembers().size}${ChatColor.GRAY}/${gang.getMembers().size}"

        FancyMessage("${ChatColor.RED}${ChatColor.BOLD}${gang.name} ${ChatColor.GRAY}[${playersOnline}${ChatColor.GRAY}]")
            .then(" ${ChatColor.GRAY}- [")
            .then("${ChatColor.DARK_AQUA}${ChatColor.BOLD}VISIT HQ")
            .command("/gang visit ${gang.name}")
            .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to visit this gang's headquarters"))
            .then("${ChatColor.GRAY}]")
            .send(sender)

        sender.sendMessage("${ChatColor.GRAY} Announcement: ${ChatColor.RED}${gang.announcement}")
        sender.sendMessage("${ChatColor.GRAY} Leader: ${ChatColor.WHITE}${gang.getLeaderUsername()}")

        val coLeaders = gang.getMembers().values.filter { it.role == GangMember.Role.CO_LEADER }
        if (coLeaders.isNotEmpty()) {
            sender.sendMessage("${ChatColor.GRAY} Co-Leaders: ${ChatColor.WHITE}${coLeaders.joinToString { getFormattedMemberName(gang, it) }}")
        }

        val captains = gang.getMembers().values.filter { it.role == GangMember.Role.CAPTAIN }
        if (captains.isNotEmpty()) {
            sender.sendMessage("${ChatColor.GRAY} Captains: ${ChatColor.WHITE}${coLeaders.joinToString { getFormattedMemberName(gang, it) }}")
        }

        val members = gang.getMembers().values.filter { it.role == GangMember.Role.MEMBER }
        if (members.isNotEmpty()) {
            sender.sendMessage("${ChatColor.GRAY} Members: ${ChatColor.WHITE}${members.joinToString { getFormattedMemberName(gang, it) }}")
        }

        sender.sendMessage("${ChatColor.GRAY} Trophies: ${ChatColor.RED}${NumberUtils.format(gang.getTrophies())}")
        sender.sendMessage("${ChatColor.GRAY} Value: ${ChatColor.RED}${NumberUtils.format(gang.cachedValue)}")
        sender.sendMessage("${ChatColor.GRAY}${Constants.LONG_LINE}")
    }

    private fun getFormattedMemberName(gang: Gang, member: GangMember): String {
        val username = member.getUsername()

        val role = if (gang.isLeader(member.uuid)) {
            "**"
        } else {
            ""
        }

        return if (Bukkit.getPlayer(member.uuid) == null) {
            "${ChatColor.RED}$role$username"
        } else {
            "${ChatColor.GREEN}$role$username"
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangSetAnnouncementCommand {

    @Command(
        names = ["gang announcement", "gang set-announcement", "gangs announcement", "gangs set-announcement"],
        description = "Set your gang's announcement"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "announcement", wildcard = true) announcement: String) {
        val gang = GangHandler.getVisitingGang(player)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to update its announcement.")
            return
        }

        if (gang.leader != player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Only the leader can update the gang's announcement.")
            return
        }

        gang.announcement = announcement
        gang.sendMessagesToMembers("${ChatColor.YELLOW}${player.name} has updated the gang's announcement.")

        player.sendMessage("${ChatColor.GREEN}Successfully updated the gang's announcement!")
    }

}
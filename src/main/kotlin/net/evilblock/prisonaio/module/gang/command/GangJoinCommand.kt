/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangJoinCommand {

    @Command(
        names = ["gang join", "gangs join", "gang accept", "gangs accept"],
        description = "Join a gang you've been invited to",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "gang") gang: Gang) {
        val maxGangs = GangModule.getMaxCellsPerPlayer()
        if (GangHandler.getAccessibleGangs(player.uniqueId).size >= maxGangs) {
            player.sendMessage("${ChatColor.RED}You can only join $maxGangs ${TextUtil.pluralize(maxGangs, "gang", "gangs")} at once.")
            return
        }

        if (gang.isMember(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You are already a member of that gang.")
            return
        }

        if (!gang.isInvited(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You haven't been invited to join that gang.")
            return
        }

        if (gang.getMembers().size >= GangModule.getMaxMembers()) {
            player.sendMessage("${ChatColor.RED}That gang has the maximum amount of members. Somebody will have to leave or be kicked for you to be able to join.")
            return
        }

        gang.memberJoin(player.uniqueId)

        Tasks.sync {
            GangHandler.attemptJoinSession(player, gang)
        }
    }

}
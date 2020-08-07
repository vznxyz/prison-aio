/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangLeaveCommand {

    @Command(
        names = ["gang leave", "gangs leave"],
        description = "Leave a gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val visitingGang = GangHandler.getAssumedGang(player.uniqueId)
        if (visitingGang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to leave it.")
            return
        }

        if (visitingGang.owner == player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Owners must disband their gang instead of leaving. Try `/gang disband`.")
            return
        }

        if (visitingGang.isActivePlayer(player)) {
            GangHandler.updateVisitingGang(player, null)

            Tasks.sync {
                player.teleport(Bukkit.getWorlds()[0].spawnLocation)
            }
        }

        visitingGang.memberLeave(player.uniqueId)
    }

}
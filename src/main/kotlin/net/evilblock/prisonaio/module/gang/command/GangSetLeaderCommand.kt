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
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object GangSetLeaderCommand {

    @Command(
        names = ["gang leader", "gangs leader"],
        description = "Relinquish leadership of your gang to a member",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") newLeader: UUID) {
        val gang = GangHandler.getGangByPlayer(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to set relinquish leadership of it.")
            return
        }

        if (!gang.isLeader(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the leader can relinquish leadership of the gang.")
            return
        }

        if (player.uniqueId == newLeader) {
            player.sendMessage("${ChatColor.RED}You are already the leader of your gang.")
            return
        }

        val newLeaderUsername = Cubed.instance.uuidCache.name(newLeader)

        if (!gang.isMember(newLeader)) {
            player.sendMessage("${ChatColor.RED}$newLeaderUsername is not a member of your gang.")
            return
        }

        gang.updateLeader(newLeader)
        player.sendMessage("${ChatColor.GREEN}You have given leadership of your gang to $newLeaderUsername.")
    }

}
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
import net.evilblock.prisonaio.module.gang.GangMember
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object GangKickCommand {

    @Command(
        names = ["gang kick", "gangs kick"],
        description = "Kick a player from your gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") kickTarget: UUID) {
        val gang = GangHandler.getGangByPlayer(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be in a gang to kick a player from it.")
            return
        }

        if (gang.getMemberInfo(player.uniqueId)?.role?.isAtLeast(GangMember.Role.CO_LEADER) == false) {
            player.sendMessage("${ChatColor.RED}You must be at least a co-leader to kick members from the gang.")
            return
        }

        if (player.uniqueId == kickTarget) {
            player.sendMessage("${ChatColor.RED}You can't kick yourself from your gang.")
            return
        }

        if (!gang.isMember(kickTarget)) {
            player.sendMessage("${ChatColor.RED}That player is not a member of your gang.")
            return
        }

        if (gang.getMemberInfo(kickTarget)!!.role.isAtLeast(gang.getMemberInfo(player.uniqueId)!!.role)) {
            player.sendMessage("${ChatColor.RED}You can't kick a member that is the same role as you.")
            return
        }

        if (gang.isLeader(kickTarget)) {
            player.sendMessage("${ChatColor.RED}You can't kick the leader of your gang!")
            return
        }

        gang.kickMember(kickTarget)

        val playerName = Cubed.instance.uuidCache.name(kickTarget)
        player.sendMessage("${ChatColor.GREEN}Successfully kicked $playerName from the gang.")
    }

}
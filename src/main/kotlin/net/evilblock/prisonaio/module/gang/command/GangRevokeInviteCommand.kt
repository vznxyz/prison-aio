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

object GangRevokeInviteCommand {

    @Command(
        names = ["gang revoke-invite", "gangs revoke-invite"],
        description = "Revoke a player's invitation to join"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") playerUuid: UUID) {
        val visitingGang = GangHandler.getVisitingGang(sender)
        if (visitingGang == null) {
            sender.sendMessage("${ChatColor.RED}You must be inside a gang to revoke player invitations.")
            return
        }

        if (visitingGang.leader != sender.uniqueId) {
            sender.sendMessage("${ChatColor.RED}Only the leader can revoke player invitations.")
            return
        }

        if (!visitingGang.isInvited(playerUuid)) {
            sender.sendMessage("${ChatColor.RED}That player hasn't been invited to join the gang.")
            return
        }

        visitingGang.revokeInvite(playerUuid)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        sender.sendMessage("${ChatColor.GREEN}Successfully revoked $playerName's invitation.")
    }

}
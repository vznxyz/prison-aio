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

object GangInviteCommand {

    @Command(
        names = ["gang invite", "gangs invite"],
        description = "Invite a player to your gang"
    )
    @JvmStatic
    fun execute(sender: Player, @Param(name = "player") playerUuid: UUID) {
        val gang = GangHandler.getAssumedGang(sender.uniqueId)
        if (gang == null) {
            sender.sendMessage("${ChatColor.RED}You must be inside your gang to invite other players.")
            return
        }

        if (gang.owner != sender.uniqueId) {
            sender.sendMessage("${ChatColor.RED}Only the owner of the gang can invite other players.")
            return
        }

        if (gang.isMember(playerUuid)) {
            sender.sendMessage("${ChatColor.RED}That player is already a member of the gang.")
            return
        }

        if (gang.isInvited(playerUuid)) {
            sender.sendMessage("${ChatColor.RED}That player has already been invited to the gang.")
            return
        }

        gang.invitePlayer(playerUuid, sender.uniqueId)

        val playerInvitedName = Cubed.instance.uuidCache.name(playerUuid)
        sender.sendMessage("${ChatColor.GREEN}You've invited $playerInvitedName to the gang.")
    }

}
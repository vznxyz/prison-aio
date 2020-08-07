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

object GangKickCommand {

    @Command(
        names = ["gang kick", "gangs kick"],
        description = "Kick a player from your gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") playerUuid: UUID) {
        val gang = GangHandler.getAssumedGang(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to kick a player from it.")
            return
        }

        if (!gang.isOwner(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the owner can kick players from the gang.")
            return
        }

        if (player.uniqueId == playerUuid) {
            player.sendMessage("${ChatColor.RED}You can't kick yourself from the gang.")
            return
        }

        if (!gang.isMember(playerUuid)) {
            player.sendMessage("${ChatColor.RED}That player is not a member of the gang.")
            return
        }

        gang.kickMember(playerUuid)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        player.sendMessage("${ChatColor.GREEN}Successfully kicked $playerName from the gang.")
    }

}
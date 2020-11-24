/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangMember
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object GangForceJoinCommand {

    @Command(
        names = ["gang admin force-join", "gangs admin force-join"],
        description = "Add a player to a gang",
        permission = Permissions.GANGS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "gang") gang: Gang, @Param(name = "player") playerUuid: UUID) {
        if (GangHandler.getAssumedGang(playerUuid) != null) {
            player.sendMessage("${ChatColor.RED}That player is already in a gang!")
            return
        }

        if (gang.isMember(playerUuid)) {
            player.sendMessage("${ChatColor.RED}That player is already a member of the gang.")
            return
        }

        gang.addMember(GangMember(playerUuid))
        GangHandler.updateGangAccess(uuid = playerUuid, gang = gang, joinable = true)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        gang.sendMessagesToMembers("${ChatColor.YELLOW}$playerName has been forcefully added to the gang.")
        player.sendMessage("${ChatColor.GREEN}Successfully added $playerName to the gang.")
    }

}
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
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object GangForceKickCommand {

    @Command(
        names = ["gang admin force-kick", "gangs admin force-kick"],
        description = "Kick a player from a gang",
        permission = Permissions.GANGS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "gang") gang: Gang, @Param(name = "player") playerUuid: UUID) {
        if (!gang.isMember(playerUuid)) {
            player.sendMessage("${ChatColor.RED}That player is not a member of the gang.")
            return
        }

        gang.kickMember(playerUuid)

        val playerName = Cubed.instance.uuidCache.name(playerUuid)
        player.sendMessage("${ChatColor.GREEN}Successfully kicked $playerName from the gang.")
    }

}
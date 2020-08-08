/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangVisitCommand {

    @Command(
        names = ["gang visit", "gang v", "gangs visit", "gangs v"],
        description = "Visit a gang's headquarters"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player", defaultValue = "self") gang: Gang) {
        if (GangHandler.getAccessibleGangs(player.uniqueId).contains(gang)) {
            player.sendMessage("${ChatColor.RED}If you want to teleport to your gang's HQ, use the `/gang hq` command.")
            return
        }

        // attemptJoinSession handles the ALLOW_VISITORS permission test so we don't have to
        GangHandler.attemptJoinSession(player, gang)
    }

}
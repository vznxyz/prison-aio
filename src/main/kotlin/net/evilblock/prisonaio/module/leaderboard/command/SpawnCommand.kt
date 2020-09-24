/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.npc.LeaderboardNpcEntity
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(
        names = ["npc spawn leaderboard", "leaderboard spawn", "leaderboards spawn", "lb spawn"],
        description = "Spawn a leaderboard NPC at your location",
        permission = Permissions.LEADERBOARDS_SPAWN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "leaderboard") leaderboard: Leaderboard) {
        val npc = LeaderboardNpcEntity(leaderboard, player.location)
        npc.initializeData()

        EntityManager.trackEntity(npc)

        player.sendMessage("${ChatColor.GREEN}Successfully spawned a ${leaderboard.name} ${ChatColor.GREEN}leaderboard NPC!")
    }

}
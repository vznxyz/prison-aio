/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.command

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.leaderboard.npc.LeaderboardNpcEntity
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnLeaderboardCommand {

    @Command(
        names = ["lb spawn", "leaderboards spawn", "leaderboard spawn"],
        description = "",
        permission = Permissions.LEADERBOARDS_SPAWN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        player.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}OFFICIAL LEADERBOARDS")

        for (leaderboard in LeaderboardsModule.getLeaderboards()) {
            val message = FancyMessage("${ChatColor.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.RED}${ChatColor.BOLD}${leaderboard.name} ")
                .then(" ")
                .then("${ChatColor.GRAY}[${ChatColor.AQUA}${ChatColor.BOLD}SPAWN${ChatColor.GRAY}]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to spawn this leaderboard NPC."))
                .command("/lb spawn ${leaderboard.id}")

            message.send(player)
        }
    }

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
        npc.spawn(player)

        EntityManager.trackEntity(npc)
        EntityManager.saveData()

        player.sendMessage("${ChatColor.GREEN}Spawned a ${leaderboard.name} ${ChatColor.GREEN}leaderboard NPC!")
    }

}
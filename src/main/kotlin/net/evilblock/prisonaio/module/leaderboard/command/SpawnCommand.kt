package net.evilblock.prisonaio.module.leaderboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.npc.LeaderboardNpcEntity
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(
        names = ["leaderboards spawn", "lb spawn"],
        description = "Spawn a leaderboard NPC",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "leaderboard") leaderboard: Leaderboard) {
        val npc = LeaderboardNpcEntity(leaderboard, player.location)
        npc.initializeData()

        EntityManager.trackEntity(npc)

        player.sendMessage("${ChatColor.GREEN}Successfully spawned a ${ChatColor.RED}${ChatColor.BOLD}${leaderboard.name} ${ChatColor.GREEN}leaderboard NPC!")
    }

}
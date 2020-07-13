/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Constants
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.NumberFormat

object PrestigeCommand {

    @Command(
        names = ["prestige"],
        description = "Enter the next prestige",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (user.getRank() != RankHandler.getLastRank()) {
            player.sendMessage("${ChatColor.RED}You must be the ${RankHandler.getLastRank().displayName} ${ChatColor.RED}rank to prestige.")
            return
        }

        if (user.getPrestige() >= RanksModule.getMaxPrestige()) {
            player.sendMessage("${ChatColor.RED}You have achieved the maximum prestige possible.")
            return
        }

        if (user.statistics.getBlocksMined() < user.getPrestigeRequirement()) {
            val newRequirement = user.getPrestigeRequirement()
            val formattedRequirement = NumberFormat.getInstance().format(newRequirement)

            player.sendMessage("${ChatColor.RED}You must meet the following requirements to prestige:")
            player.sendMessage("${ChatColor.GRAY}${Constants.DOT_SYMBOL} Mine $formattedRequirement blocks")
            return
        }

        val prestigeEvent = AsyncPlayerPrestigeEvent(player, user, user.getPrestige(), user.getPrestige() + 1)
        Bukkit.getPluginManager().callEvent(prestigeEvent)

        if (prestigeEvent.isCancelled) {
            return
        }

        user.updatePrestige(prestigeEvent.to)
        user.updateRank(RankHandler.getStartingRank())

        for (command in RanksModule.getPrestigeCommands()) {
            val translatedCommand = command
                .replace("{playerName}", player.name)
                .replace("{playerUuid}", player.uniqueId.toString())
                .replace("{prestige}", prestigeEvent.to.toString())

            Tasks.sync {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), translatedCommand)
            }
        }

        player.sendMessage("")
        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Entered Next Prestige")
        player.sendMessage(" ${ChatColor.GRAY}Congratulations on entering the next prestige! Your")
        player.sendMessage(" ${ChatColor.GRAY}rank has been reset to ${RankHandler.getStartingRank().displayName} ${ChatColor.GRAY}for you to rankup again.")
        player.sendMessage("")

        if (prestigeEvent.to != RanksModule.getMaxPrestige()) {
            val newRequirement = user.getPrestigeRequirement()
            val formattedRequirement = NumberFormat.getInstance().format(newRequirement)

            if (user.statistics.getBlocksMined() >= newRequirement) {
                player.sendMessage(" ${ChatColor.GRAY}You already meet the requirement of mining $formattedRequirement blocks to enter the next prestige.")
            } else {
                player.sendMessage(" ${ChatColor.GRAY}To enter the next prestige, you need to meet the")
                player.sendMessage(" ${ChatColor.GRAY}following requirements${ChatColor.GRAY}:")
                player.sendMessage(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} Mine $formattedRequirement blocks")
            }

            player.sendMessage("")
        }

        Tasks.sync {
            // teleport the player to spawn because they can no longer access certain mines
            player.teleport(Bukkit.getWorlds()[0].spawnLocation)
        }
    }

}
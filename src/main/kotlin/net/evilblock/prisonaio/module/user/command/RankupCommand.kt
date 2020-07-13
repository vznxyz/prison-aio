/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RankupCommand {

    @Command(
        names = ["rankup"],
        description = "Purchase the next rankup"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)

        val optionalNextRank = RankHandler.getNextRank(user.getRank())
        if (!optionalNextRank.isPresent) {
            player.sendMessage("${ChatColor.RED}You have achieved max rank and cannot rankup anymore. Try /prestige!")
            return
        }

        val rank = optionalNextRank.get()
        val rankPrice = rank.getPrice(user.getPrestige())

        if (user.getMoneyBalance() >= rankPrice) {
            val previousRank = user.getRank()

            val playerRankupEvent = PlayerRankupEvent(player, previousRank, rank)
            Bukkit.getServer().pluginManager.callEvent(playerRankupEvent)

            if (playerRankupEvent.isCancelled) {
                return
            }

            VaultHook.useEconomy { it.withdrawPlayer(player, rankPrice) }

            user.updateRank(rank)
            user.applyPermissions(player)

            rank.executeCommands(player)

            val moneyNeeded = NumberUtils.format(rankPrice)

            player.sendMessage("")
            player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Rankup Purchased")
            player.sendMessage(" ${ChatColor.GRAY}Congratulations on your rankup from ${previousRank.displayName} ${ChatColor.GRAY}to ${rank.displayName}${ChatColor.GRAY}!")
            player.sendMessage(" ${ChatColor.GRAY}The rankup cost ${ChatColor.GREEN}$${ChatColor.YELLOW}$moneyNeeded${ChatColor.GRAY}.")
            player.sendMessage("")
        } else {
            val moneyNeeded = NumberUtils.format((rankPrice - user.getMoneyBalance()).toLong())
            player.sendMessage("")
            player.sendMessage(" ${ChatColor.RED}${ChatColor.BOLD}Cannot Afford Rankup")
            player.sendMessage(" ${ChatColor.GRAY}You need ${ChatColor.GREEN}$${ChatColor.YELLOW}$moneyNeeded ${ChatColor.GRAY}more to rankup to ${rank.displayName}${ChatColor.GRAY}.")
            player.sendMessage("")
        }
    }

}
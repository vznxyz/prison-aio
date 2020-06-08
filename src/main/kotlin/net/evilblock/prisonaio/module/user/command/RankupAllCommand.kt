package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RankupAllCommand {

    @Command(names = ["rankup all", "rankupall"], description = "Rankup as many levels as you can")
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        val previousRank = user.getCurrentRank()

        val optionalNextRank = RankHandler.getNextRank(previousRank)
        if (!optionalNextRank.isPresent) {
            player.sendMessage("${ChatColor.RED}You have achieved max rank and cannot rankup anymore. Try /prestige!")
            return
        }

        var balance = VaultHook.useEconomyAndReturn { economy -> economy.getBalance(player) }

        val purchasedRanks = arrayListOf<Rank>()
        for (rank in RankHandler.getSortedRanks()) {
            if (previousRank.sortOrder >= rank.sortOrder) {
                continue
            }

            val rankPrice = rank.getPrice(user.getCurrentPrestige())
            if (rankPrice > balance) {
                break
            }

            val playerRankupEvent = PlayerRankupEvent(player, previousRank, rank)
            Bukkit.getServer().pluginManager.callEvent(playerRankupEvent)

            if (playerRankupEvent.isCancelled) {
                return
            }

            VaultHook.useEconomy { economy ->
                val response = economy.withdrawPlayer(player, rankPrice.toDouble())
                if (!response.transactionSuccess()) {
                    return@useEconomy
                }

                balance -= rankPrice

                user.updateCurrentRank(rank)
                rank.executeCommands(player)

                purchasedRanks.add(rank)
            }
        }

        user.applyPermissions(player)

        if (purchasedRanks.isEmpty()) {
            player.sendMessage("")
            player.sendMessage(" ${ChatColor.RED}${ChatColor.BOLD}Cannot Afford Rankup")
            player.sendMessage(" ${ChatColor.GRAY}You don't have enough money to purchase any rankups.")
            player.sendMessage("")
            return
        }

        player.sendMessage("")
        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Rankups Purchased")
        player.sendMessage(" ${ChatColor.GRAY}Congratulations on your rankups from ${previousRank.displayName} ${ChatColor.GRAY}to ${user.getCurrentRank().displayName}${ChatColor.GRAY}!")

        val formattedMoneySpent = NumberUtils.format(purchasedRanks.map { it.getPrice(user.getCurrentPrestige()) }.sum())
        player.sendMessage(" ${ChatColor.GRAY}The rankups cost ${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedMoneySpent${ChatColor.GRAY}.")

        player.sendMessage("")
    }

}
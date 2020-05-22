package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.event.PlayerPrestigeEvent
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PrestigeCommand {

    @Command(
        names = ["prestige"],
        description = "Enter the next prestige"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (user.getCurrentRank() != RankHandler.getLastRank()) {
            player.sendMessage("${ChatColor.RED}You must be the ${RankHandler.getLastRank().displayName} ${ChatColor.RED}rank to prestige.")
            return
        }

        if (user.getCurrentPrestige() >= UsersModule.getMaxPrestige()) {
            player.sendMessage("${ChatColor.RED}You have achieved the maximum prestige possible.")
            return
        }

        val prestigeEvent = PlayerPrestigeEvent(player, user.getCurrentPrestige(), user.getCurrentPrestige() + 1)
        Bukkit.getPluginManager().callEvent(prestigeEvent)

        if (!prestigeEvent.isCancelled) {
            user.updateCurrentPrestige(prestigeEvent.to)
            user.updateCurrentRank(RankHandler.getStartingRank())

            player.sendMessage("")
            player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Entered Next Prestige")
            player.sendMessage(" ${ChatColor.GRAY}Congratulations on entering the next prestige! Your")
            player.sendMessage(" ${ChatColor.GRAY}rank has been reset to ${RankHandler.getStartingRank().displayName} ${ChatColor.GRAY}for you to rankup again.")
            player.sendMessage("")
        }
    }

}
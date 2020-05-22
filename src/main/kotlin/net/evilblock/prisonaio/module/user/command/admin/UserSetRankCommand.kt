package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Constants
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object UserSetRankCommand {

    @Command(
        names = ["user setrank"],
        description = "Update a user's rank",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "rank") rank: Rank) {
        user.updateCurrentRank(rank)
        UserHandler.saveUser(user)

        val player = Bukkit.getPlayer(user.uuid)
        if (player != null) {
            if (sender is Player) {
                player.sendMessage("${ChatColor.GREEN}Your rank has been manually updated to ${rank.displayName} ${ChatColor.GREEN}by ${ChatColor.YELLOW}${sender.name}${ChatColor.GREEN}.")
            } else {
                player.sendMessage("${ChatColor.GREEN}Your rank has been manually updated to ${rank.displayName}${ChatColor.GREEN}.")
            }
        }

        sender.sendMessage("${Constants.ADMIN_PREFIX}You've set ${ChatColor.YELLOW}${user.getUsername()}${ChatColor.GRAY}'s rank to ${rank.displayName}${ChatColor.GRAY}.")
    }

}
package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.util.Constants
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object UserSetPrestigeCommand {

    @Command(
        names = ["user setprestige"],
        description = "Update a user's prestige",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "prestige") prestige: Int) {
        assert(prestige > 0) { "Cannot set prestige to less than 0" }
        assert(prestige > 0) { "Cannot set prestige to more than ${RanksModule.getMaxPrestige()}" }

        user.updateCurrentPrestige(prestige)
        UserHandler.saveUser(user)

        val player = Bukkit.getPlayer(user.uuid)
        if (player != null) {
            if (sender is Player) {
                player.sendMessage("${ChatColor.GREEN}Your prestige has been manually updated to $prestige ${ChatColor.GREEN}by ${ChatColor.YELLOW}${sender.name}${ChatColor.GREEN}.")
            } else {
                player.sendMessage("${ChatColor.GREEN}Your prestige has been manually updated to $prestige${ChatColor.GREEN}.")
            }
        }

        sender.sendMessage("${Constants.ADMIN_PREFIX}You've set ${ChatColor.YELLOW}${user.getUsername()}${ChatColor.GRAY}'s prestige to $prestige${ChatColor.GRAY}.")
    }

}
package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object UserStatisticsCommands {

    @Command(
        names = ["user setstat blocks-mined"],
        description = "Set the blocks-mined statistic of a user",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "amount") amount: Int) {
        assert(amount > 0) { "Number must be above 0" }

        user.statistics.setBlocksMined(amount)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}Updated ${user.getUsername()}'s blocks-mined statistic to $amount.")
    }

}
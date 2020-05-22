package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.profile.menu.ProfileStatisticsMenu
import org.bukkit.entity.Player

object ProfileCommand {

    @Command(
        names = ["profile", "prof", "u", "user", "stats", "statistics"],
        description = "View a player's profile",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player", defaultValue = "self") user: User) {
        ProfileStatisticsMenu(user).openMenu(player)
    }

}
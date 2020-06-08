package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.profile.menu.tab.ProfileCommentsMenu
import net.evilblock.prisonaio.module.user.profile.menu.tab.ProfileStatisticsMenu
import org.bukkit.entity.Player

object ProfileCommand {

    @Command(
        names = ["profile", "prof", "u", "user", "stats", "statistics"],
        description = "View a player's profile",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player", defaultValue = "self") user: User, @Param(name = "tab", defaultValue = DEFAULT_TAB) tab: String) {
        if (tab == DEFAULT_TAB) {
            ProfileStatisticsMenu(user).openMenu(player)
        } else {
            when (tab.toLowerCase()) {
                "comments" -> {
                    ProfileCommentsMenu(user).openMenu(player)
                }
            }
        }
    }

    private const val DEFAULT_TAB = "__DEFAULT__"

}
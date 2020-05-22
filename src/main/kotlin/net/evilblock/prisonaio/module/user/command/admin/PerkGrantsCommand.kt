package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.perk.menu.GrantedPerksMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object PerkGrantsCommand {

    @Command(
        names = ["user perks grants"],
        description = "View a user's perk grants",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player", defaultValue = "self") target: User) {
        GrantedPerksMenu(target).openMenu(player)
    }

}
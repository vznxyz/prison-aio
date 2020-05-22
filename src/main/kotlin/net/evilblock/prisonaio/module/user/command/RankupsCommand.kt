package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.UserRankupsMenu
import org.bukkit.entity.Player

object RankupsCommand {

    @Command(names = ["ranks", "rankups"], description = "Shows each rankup and information about them")
    @JvmStatic
    fun execute(player: Player) {
        UserRankupsMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
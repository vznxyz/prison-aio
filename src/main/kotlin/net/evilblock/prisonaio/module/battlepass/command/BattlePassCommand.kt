package net.evilblock.prisonaio.module.battlepass.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassMenu
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player

object BattlePassCommand {

    @Command(
        names = ["battlepass", "bp", "junkiepass", "jp"],
        description = "Open the JunkiePass"
    )
    @JvmStatic
    fun execute(player: Player) {
        BattlePassMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
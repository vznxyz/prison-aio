package net.evilblock.prisonaio.module.battlepass.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassEditorMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object BattlePassEditorCommand {

    @Command(
        names = ["battlepass editor", "bp editor", "junkiepass editor", "jp editor"],
        description = "Opens the BattlePass Editor",
        permission = Permissions.BATTLE_PASS_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        BattlePassEditorMenu().openMenu(player)
    }

}
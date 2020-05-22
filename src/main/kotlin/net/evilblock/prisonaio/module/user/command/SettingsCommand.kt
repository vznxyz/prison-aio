package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSettingsMenu
import org.bukkit.entity.Player

object SettingsCommand {

    @Command(
        names = ["settings", "options"],
        description = "Manage your account settings"
    )
    @JvmStatic
    fun execute(player: Player) {
        UserSettingsMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}
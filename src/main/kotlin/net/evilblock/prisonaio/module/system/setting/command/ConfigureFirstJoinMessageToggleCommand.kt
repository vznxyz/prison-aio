/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.setting.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.system.setting.Setting
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.command.CommandSender

object ConfigureFirstJoinMessageToggleCommand {

    @Command(
        names = ["prison configure first-join message toggle"],
        description = "Toggle the first-join message",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        val newValue = !Setting.FIRST_JOIN_MESSAGE_TOGGLE.getValue<Boolean>()
        Setting.FIRST_JOIN_MESSAGE_TOGGLE.updateValue(newValue)

        if (newValue) {
            PrisonAIO.instance.systemLog("First-join messages are now enabled")
        } else {
            PrisonAIO.instance.systemLog("First-join messages are now disabled")
        }
    }

}
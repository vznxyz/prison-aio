/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.admin.setting.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.admin.setting.Setting
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ConfigureFirstJoinMessageCommand {

    @Command(
        names = ["prison configure first-join message"],
        description = "Configure the first-join message",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "format", wildcard = true) format: String) {
        Setting.FIRST_JOIN_MESSAGE_FORMAT.updateValue(ChatColor.translateAlternateColorCodes('&', format))
        PrisonAIO.instance.systemLog("Updated first-join message format")
    }

}
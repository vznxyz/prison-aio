/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.timer.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.combat.timer.listener.CombatTimerListeners
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BlockedCommandsRemoveCommand {

    @Command(
        names = ["combat blocked-commands remove"],
        description = "Removes a command prefix from the blocked commands",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "command") command: String) {
        CombatTimerListeners.BLOCKED_COMMANDS.remove(command.toLowerCase())
        sender.sendMessage("${ChatColor.GREEN}Removed command from blocked commands!")
    }

}
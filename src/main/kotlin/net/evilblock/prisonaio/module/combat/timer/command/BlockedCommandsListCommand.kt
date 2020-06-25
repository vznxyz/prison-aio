/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.timer.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.listener.CombatTimerListeners
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.command.CommandSender

object BlockedCommandsListCommand {

    @Command(
        names = ["combat blocked-commands list"],
        description = "Lists all of the combat blocked commands",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        sender.sendMessage(CombatTimerListeners.BLOCKED_COMMANDS.joinToString())
    }

}
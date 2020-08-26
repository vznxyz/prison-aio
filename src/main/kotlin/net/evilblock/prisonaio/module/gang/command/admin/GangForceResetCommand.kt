/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object GangForceResetCommand {

    @Command(
        names = ["gang admin reset", "gangs admin reset"],
        description = "Reset a gang headquarters",
        permission = Permissions.GANGS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "gang") gang: Gang, @Param(name = "player") newLeader: UUID) {
        val newLeaderUsername = Cubed.instance.uuidCache.name(newLeader)
        if (gang.isLeader(newLeader)) {
            sender.sendMessage("${ChatColor.RED}$newLeaderUsername is already the leader of ${gang.name}.")
            return
        }

        if (!gang.isMember(newLeader)) {
            sender.sendMessage("${ChatColor.RED}$newLeaderUsername is not a member of ${gang.name}.")
            return
        }

        gang.updateLeader(newLeader)
        sender.sendMessage("${ChatColor.GREEN}You have given leadership of ${gang.name} to $newLeaderUsername.")
    }

}
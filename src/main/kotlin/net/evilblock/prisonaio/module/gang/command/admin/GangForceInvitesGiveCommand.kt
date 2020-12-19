/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GangForceInvitesGiveCommand {

    @Command(
        names = ["gang admin force-invites add", "gangs admin force-invites add"],
        description = "Give force-invites to a gang",
        permission = Permissions.GANGS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "gang") gang: Gang, @Param(name = "amount") amount: Int) {
        gang.giveForceInvites(amount)

        gang.sendMessagesToMembers("${ChatColor.YELLOW}Your gang has been given ${ChatColor.YELLOW}x${ChatColor.AQUA}${NumberUtils.format(amount)} ${ChatColor.YELLOW}force-invites!")
        sender.sendMessage("${ChatColor.YELLOW}You've given ${ChatColor.RED}${ChatColor.BOLD}${gang.name} ${ChatColor.YELLOW}x${ChatColor.AQUA}${NumberUtils.format(amount)} ${ChatColor.YELLOW}force-invites!")
    }

}
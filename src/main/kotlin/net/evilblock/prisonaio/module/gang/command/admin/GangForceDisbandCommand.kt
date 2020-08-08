/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object GangForceDisbandCommand {

    @Command(
        names = ["gang admin force-disband", "gangs admin force-disband"],
        description = "Disband a gang",
        permission = Permissions.GANGS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "gang") gang: Gang) {
        gang.sendMessagesToMembers("${ChatColor.YELLOW}The gang has been forcefully disbanded by an administrator.")

        for (member in gang.getMembers()) {
            GangHandler.updateGangAccess(member, gang, false)
        }

        gang.kickVisitors(force = true)

        GangHandler.forgetGang(gang)
        RegionsModule.clearBlockCache(gang)

        sender.sendMessage("${ChatColor.GREEN}You have successfully disbanded `${gang.name}`.")
    }

}
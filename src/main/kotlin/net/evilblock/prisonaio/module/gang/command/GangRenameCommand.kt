/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangRenameCommand {

    @Command(
        names = ["gang rename", "gangs rename"],
        description = "Rename your gang"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name", wildcard = true) name: String) {
        val gang = GangHandler.getAssumedGang(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to rename it.")
            return
        }

        for (blockedName in GangHandler.BLOCKED_NAMES) {
            if (blockedName.matches(name)) {
                player.sendMessage("${ChatColor.RED}The name you input contains inappropriate content. Please try a different name.")
                return
            }
        }

        if (!gang.isLeader(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Only the leader can rename the gang.")
            return
        }

        if (GangHandler.getGangByName(name) != null) {
            player.sendMessage("${ChatColor.RED}The name `$name` is already taken by another gang.")
            return
        }

        if (name.length > GangModule.getMaxNameLength()) {
            player.sendMessage("${ChatColor.RED}A gang's name can only be ${GangModule.getMaxNameLength()} characters long. The name you entered was ${name.length} characters.")
            return
        }

        GangHandler.renameGang(gang, name)
        gang.sendMessagesToMembers("${ChatColor.YELLOW}The gang has been renamed to `$name` by ${player.name}!")
    }

}
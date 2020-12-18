/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangDisbandCommand {

    @Command(
        names = ["gang disband", "gangs disband"],
        description = "Disband your gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val gang = GangHandler.getGangByPlayer(player.uniqueId)
        if (gang == null) {
            player.sendMessage("${ChatColor.RED}You must be in a gang to disband it.")
            return
        }

        if (!gang.isLeader(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You must be the leader of this gang to disband it.")
            return
        }

        ConfirmMenu { confirmed ->
            if (confirmed) {
                GangHandler.disbandGang(gang)
            }
        }.openMenu(player)

        player.sendMessage("${ChatColor.GREEN}You have successfully disbanded your gang.")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangSetHomeCommand {

    @Command(
        names = ["gang sethome", "gangs sethome", "gang sethq", "gangs sethq"],
        description = "Set your gang's HQ"
    )
    @JvmStatic
    fun execute(player: Player) {
        val visitingGang = GangHandler.getVisitingGang(player)
        if (visitingGang == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a gang to set its HQ location.")
            return
        }

        if (visitingGang.owner != player.uniqueId) {
            player.sendMessage("${ChatColor.RED}Only the owner of the gang can set the HQ location.")
            return
        }

        if (!visitingGang.cuboid.contains(player.location)) {
            player.sendMessage("${ChatColor.RED}You can only set the HQ location to somewhere within the gang headquarters.")
            return
        }

        visitingGang.homeLocation = player.location
        visitingGang.sendMessagesToMembers("${ChatColor.YELLOW}${player.name} updated the gang's HQ.")

        player.sendMessage("${ChatColor.GREEN}Successfully updated the gang's HQ location.")
    }

}
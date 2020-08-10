/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.challenge.menu.GangChallengesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangChallengesCommand {

    @Command(
        names = ["gang challenges", "gangs challenges"],
        description = "View your gang's challenges"
    )
    @JvmStatic
    fun execute(player: Player) {
        val assumedGang = GangHandler.getAssumedGang(player.uniqueId)
        if (assumedGang != null) {
            GangChallengesMenu(assumedGang).openMenu(player)
        } else {
            player.sendMessage("${ChatColor.RED}You're not in a gang!")
        }
    }

}
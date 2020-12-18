/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.menu.BoostersMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangBoostersCommand {

    @Command(
        names = ["gang boosters", "gangs boosters", "gang booster", "gangs booster"],
        description = "View and purchase gang boosters"
    )
    @JvmStatic
    fun execute(player: Player) {
        val assumedGang = GangHandler.getGangByPlayer(player.uniqueId)
        if (assumedGang == null) {
            player.sendMessage("${ChatColor.RED}You must be in a gang to access Gang Boosters.")
            return
        }

        BoostersMenu(assumedGang).openMenu(player)
    }

}
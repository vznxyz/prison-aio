/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.menu.HomesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangHomeCommand {

    @Command(names = ["gang home", "gangs home", "gang hq", "gang hqs"], description = "Teleport to your gang HQ")
    @JvmStatic
    fun execute(player: Player) {
        val accessibleGangs = GangHandler.getAccessibleGangs(player.uniqueId)
        if (accessibleGangs.isEmpty()) {
            player.sendMessage("${ChatColor.RED}You aren't a member of any gangs.")
            return
        }

        if (accessibleGangs.size > 1) {
            HomesMenu().openMenu(player)
            return
        }

        player.sendMessage("${ChatColor.YELLOW}Teleporting you to your gang HQ...")

        val gang = accessibleGangs.first()
        GangHandler.attemptJoinSession(player, gang)
    }

}
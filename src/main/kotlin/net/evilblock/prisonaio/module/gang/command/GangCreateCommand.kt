/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object GangCreateCommand {

    @Command(
        names = ["gang create", "gangs create"],
        description = "Create a new gang",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name", wildcard = true) name: String) {
        if (GangHandler.getGangByPlayer(player) != null) {
            player.sendMessage("${ChatColor.RED}You already belong to a gang!")
            return
        }

        try {
            GangHandler.createNewGang(player, name) { gang ->
                Tasks.sync {
                    GangHandler.attemptJoinSession(player, gang)
                    player.sendMessage("${ChatColor.YELLOW}You are now the leader of this gang. Use ${ChatColor.YELLOW}/gang home ${ChatColor.YELLOW}to teleport back to your gang headquarters.")
                }
            }
        } catch (e: Exception) {
            if (player.isOp) {
                player.sendMessage("${ChatColor.RED}Failed to generate a new gang for you: ${e.message}")
            } else {
                player.sendMessage("${ChatColor.RED}Failed to generate a new gang for you. If this issue persists, please contact an administrator.")
            }
            return
        }
    }

}
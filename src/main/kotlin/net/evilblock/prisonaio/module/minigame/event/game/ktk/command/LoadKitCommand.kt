/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.ktk.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.minigame.event.config.EventConfigHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object LoadKitCommand {

    @Command(
        names = ["events load-kit", "event load-kit"],
        description = "Load an event kit",
        permission = Permissions.EVENTS_EDIT
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "kit") kit: String) {
        when {
            kit.equals("king", ignoreCase = true) -> {
                if (EventConfigHandler.config.ktkKingKit == null) {
                    player.sendMessage("${ChatColor.RED}The king kit hasn't been set!")
                } else {
                    EventConfigHandler.config.ktkKingKit!!.giveToPlayer(player)
                    player.sendMessage("${ChatColor.GREEN}You've been given the king kit!")
                }
            }
            kit.equals("attacker", ignoreCase = true) -> {
                if (EventConfigHandler.config.ktkAttackerKit == null) {
                    player.sendMessage("${ChatColor.RED}The attacker kit hasn't been set!")
                } else {
                    EventConfigHandler.config.ktkAttackerKit!!.giveToPlayer(player)
                    player.sendMessage("${ChatColor.GREEN}You've been given the attacker kit!")
                }
            }
            else -> {
                player.sendMessage("${ChatColor.RED}Invalid kit type!")
            }
        }
    }

}
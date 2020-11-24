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
import net.evilblock.prisonaio.module.minigame.event.game.util.SimpleKit
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SaveKitCommand {

    @Command(
        names = ["events save-kit", "event save-kit"],
        description = "Save an event kit",
        permission = Permissions.EVENTS_EDIT
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "kit") kit: String) {
        when {
            kit.equals("king", ignoreCase = true) -> {
                EventConfigHandler.config.ktkKingKit = SimpleKit(player.inventory.storageContents, player.inventory.armorContents)
                EventConfigHandler.saveData()

                player.sendMessage("${ChatColor.GREEN}Saved king kit!")
            }
            kit.equals("attacker", ignoreCase = true) -> {
                EventConfigHandler.config.ktkAttackerKit = SimpleKit(player.inventory.storageContents, player.inventory.armorContents)
                EventConfigHandler.saveData()

                player.sendMessage("${ChatColor.GREEN}Saved attacker kit!")
            }
            else -> {
                player.sendMessage("${ChatColor.RED}Invalid kit type!")
            }
        }
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(
        names = ["spawn"],
        description = "Teleport to spawn"
    )
    @JvmStatic
    fun execute(player: Player) {
        player.teleport(Bukkit.getServer().worlds[0].spawnLocation)
        player.sendMessage("${ChatColor.YELLOW}You have been teleported to spawn.")
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object HelpCommand {

    @Command(names = ["privatemine help", "pmine help"])
    @JvmStatic
    fun execute(player: Player) {
        for (line in PrivateMinesModule.config.getStringList("language.help")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line))
        }
    }

}
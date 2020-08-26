/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EventGameTypeParameterType : ParameterType<EventGameType> {

    override fun transform(sender: CommandSender, source: String): EventGameType? {
        return try {
            EventGameType.valueOf(source)
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a game type from the input `$source`.")
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()
        for (gameType in EventGameType.values()) {
            if (gameType.name.toLowerCase().startsWith(source, ignoreCase = true)) {
                completions.add(gameType.name)
            }
        }
        return completions
    }

}
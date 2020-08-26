/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.arena.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArenaHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EventGameArenaParameterType : ParameterType<EventGameArena?> {

    override fun transform(sender: CommandSender, source: String): EventGameArena? {
        val arena: EventGameArena? = EventGameArenaHandler.getArenaByName(source)
        if (arena == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find an arena with the name '$source'.")
            return null
        }
        return arena
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()
        for (arena in EventGameArenaHandler.getArenas()) {
            if (arena.name.startsWith(source, ignoreCase = true)) {
                completions.add(arena.name)
            }
        }
        return completions
    }

}
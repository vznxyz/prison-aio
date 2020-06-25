/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MineParameterType : ParameterType<Mine?> {

    override fun transform(sender: CommandSender, source: String): Mine? {
        val optionalMine = MineHandler.getMineById(source)

        if (!optionalMine.isPresent) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a mine by the name `${ChatColor.WHITE}$source${ChatColor.RED}`.")
            return null
        }

        return optionalMine.get()
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()

        for (mine in MineHandler.getMines()) {
            if (mine.id.toLowerCase().startsWith(source.toLowerCase())) {
                completions.add(mine.id)
            }
        }

        return completions
    }

}
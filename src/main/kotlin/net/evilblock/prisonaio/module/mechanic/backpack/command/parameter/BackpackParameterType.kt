/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BackpackParameterType : ParameterType<Backpack> {

    override fun transform(sender: CommandSender, source: String): Backpack? {
        val backpack = BackpackHandler.getBackpack(source)
        if (backpack == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a backpack with the ID `$source`.")
        }

        return backpack
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList()
    }

}
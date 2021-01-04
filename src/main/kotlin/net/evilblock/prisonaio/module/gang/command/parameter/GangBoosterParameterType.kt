/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GangBoosterParameterType : ParameterType<GangBooster.BoosterType> {

    override fun transform(sender: CommandSender, source: String): GangBooster.BoosterType? {
        try {
            return GangBooster.BoosterType.valueOf(source.toUpperCase())
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a gang booster by that name.")
        }
        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return GangBooster.BoosterType.values().map { it.name }
    }

}
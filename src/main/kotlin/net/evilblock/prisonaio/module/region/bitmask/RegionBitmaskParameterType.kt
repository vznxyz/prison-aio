/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bitmask

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class RegionBitmaskParameterType : ParameterType<RegionBitmask> {

    override fun transform(sender: CommandSender, source: String): RegionBitmask? {
        for (bitmaskType in RegionBitmask.values()) {
            if (source.equals(bitmaskType.displayName, ignoreCase = true)) {
                return bitmaskType
            }
        }

        sender.sendMessage("${ChatColor.RED}No bitmask type with the name $source found.")
        return null
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        val completions: MutableList<String> = ArrayList()
        for (bitmask in RegionBitmask.values()) {
            if (StringUtils.startsWithIgnoreCase(bitmask.displayName, source)) {
                completions.add(bitmask.displayName)
            }
        }
        return completions
    }

}
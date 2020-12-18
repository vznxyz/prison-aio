/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warps.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.warps.Warp
import net.evilblock.prisonaio.module.warps.WarpHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpParameterType : ParameterType<Warp> {

    override fun transform(sender: CommandSender, source: String): Warp? {
        val warp = WarpHandler.getWarpById(source)
        if (warp == null) {
            sender.sendMessage("${ChatColor.RED}That warp doesn't seem to exist!")
        }
        return warp
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return arrayListOf<String>().also { completions ->
            for (warp in WarpHandler.getWarps()) {
                if (warp.id.startsWith(source, ignoreCase = true)) {
                    completions.add(warp.id)
                }
            }
        }
    }

}
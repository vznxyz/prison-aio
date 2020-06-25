/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CrateParameterType : ParameterType<Crate> {

    override fun transform(sender: CommandSender, source: String): Crate? {
        val crate = CrateHandler.findCrate(source)
        if (crate == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a crate by the ID `${ChatColor.RESET}$source${ChatColor.RED}`.")
            return null
        }

        return crate
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completed = arrayListOf<String>()
        for (crate in CrateHandler.getCrates()) {
            if (crate.id.startsWith(source, ignoreCase = true)) {
                completed.add(crate.id)
            }
        }
        return completed
    }

}
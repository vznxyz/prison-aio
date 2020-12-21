/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.kits.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object KitParameterType : ParameterType<Kit> {

    override fun transform(sender: CommandSender, source: String): Kit? {
        val kit = KitHandler.getKitById(source)
        if (kit == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a kit by the ID `${ChatColor.RESET}$source${ChatColor.RED}`.")
            return null
        }

        return kit
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completed = arrayListOf<String>()
        for (kit in KitHandler.getKits()) {
            if (kit.id.startsWith(source, ignoreCase = true)) {
                completed.add(kit.id)
            }
        }
        return completed
    }

}
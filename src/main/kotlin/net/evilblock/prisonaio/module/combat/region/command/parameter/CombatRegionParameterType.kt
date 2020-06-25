/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.region.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.combat.region.CombatRegion
import net.evilblock.prisonaio.module.combat.region.CombatRegionHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CombatRegionParameterType : ParameterType<CombatRegion?> {

    override fun transform(sender: CommandSender, source: String): CombatRegion? {
        val region = CombatRegionHandler.getRegionById(source)
        if (region == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a combat region by that ID.")
        }
        return region
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()
        for (region in CombatRegionHandler.getRegions()) {
            if (region.id.startsWith(source, ignoreCase = true)) {
                completions.add(region.id)
            }
        }
        return completions
    }

}
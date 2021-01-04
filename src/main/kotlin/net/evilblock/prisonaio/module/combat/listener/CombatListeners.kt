/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.listener

import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CombatListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        val region = RegionHandler.findRegion(event.player.location)
        if (region is BitmaskRegion && region.hasBitmask(RegionBitmask.DANGER_ZONE)) {
            for (blockedCommand in CombatModule.getDisabledCommands()) {
                if (event.message.startsWith(blockedCommand, ignoreCase = true)) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}You can't execute that command in the PvP arena!")
                }
            }
        }
    }

}
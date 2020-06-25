/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.region.listener

import net.evilblock.prisonaio.module.combat.region.CombatRegion
import net.evilblock.prisonaio.module.region.RegionsModule
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CombatRegionPreventionListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        val region = RegionsModule.findRegion(event.player.location)
        if (region is CombatRegion) {
            for (blockedCommand in BLOCKED_COMMANDS) {
                if (event.message.startsWith(blockedCommand, ignoreCase = true)) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}You can't execute that command in the PvP arena!")
                }
            }
        }
    }

    private val BLOCKED_COMMANDS = arrayListOf(
        "/fly",
        "/speed",
        "/essentials:fly",
        "/essentials:speed"
    )

}
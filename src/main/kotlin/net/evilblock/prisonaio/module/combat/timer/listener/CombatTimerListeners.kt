/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.timer.listener

import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

object CombatTimerListeners : Listener {

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val timer = CombatTimerHandler.getTimer(event.player.uniqueId)
        if (timer != null) {
            CombatTimerHandler.forgetTimer(timer)
        }
    }

    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        val timer = CombatTimerHandler.getTimer(event.player.uniqueId)
        if (timer != null) {
            CombatTimerHandler.forgetTimer(timer)
        }
    }

    @EventHandler
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        val combatTimer = CombatTimerHandler.getTimer(event.player.uniqueId)
        if (combatTimer != null && !combatTimer.hasExpired()) {
            for (blockedCommand in CombatModule.getDisabledCommands()) {
                if (event.message.startsWith(blockedCommand, ignoreCase = true)) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}You can't execute that command while in combat!")
                    return
                }
            }
        }
    }

}